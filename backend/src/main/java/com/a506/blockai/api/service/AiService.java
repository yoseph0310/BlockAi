package com.a506.blockai.api.service;

import com.a506.blockai.api.dto.request.BiometricsCertificateRequest;
import com.a506.blockai.api.dto.request.FaceBiometricRequest;
import com.a506.blockai.api.dto.request.VoiceBiometricRequest;
import com.a506.blockai.api.dto.response.FaceBiometricResponse;
import com.a506.blockai.api.dto.response.VoiceBiometricResponse;
import com.a506.blockai.common.util.Base64Encoder;
import com.a506.blockai.config.AwsProperties;
import com.a506.blockai.db.entity.DID;
import com.a506.blockai.db.entity.User;
import com.a506.blockai.db.repository.UserRepository;
import com.a506.blockai.exception.DidNotYetIssuedException;
import com.a506.blockai.exception.UserNotFoundException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.java_websocket.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;


@Getter
@Service
@RequiredArgsConstructor
public class AiService {

    private final EthereumService ethereumService;
    private final AwsProperties awsProperties;
    private final AmazonS3Client amazonS3Client;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    @Autowired
    private AmazonRekognition rekognitionClient;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //?????? ??????
    private final String rootPath = System.getProperty("user.dir");
    private final String movePath = "/";

    @Bean
    public AmazonRekognition amazonRekognition() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsProperties.getAccessKey(), awsProperties.getSecretKey());

        return AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsProperties.getAccessKey(), awsProperties.getSecretKey());
        return AmazonS3Client.builder()
                .standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    /* voice detection */
    public float identifyVoice(VoiceBiometricRequest voiceBiometricsRequest) throws IOException {
        String encodedUserVoice = voiceBiometricsRequest.getEncodedUserVoice(); //??????????????? base64??? ???????????? ????????? ????????? ??????
        String savedS3UserVoiceUrl = voiceBiometricsRequest.getSavedS3UserVoiceUrl(); //DID?????? ??? ??????????????? ????????? ????????????(S3 ????????????)

        AmazonS3 amazonS3Client = amazonS3Client();
        //????????? S3??? ???????????? ???????????? ????????????
        String fileName = savedS3UserVoiceUrl.split("/")[3];
        com.amazonaws.services.s3.model.S3Object savedUserVoice = amazonS3Client.getObject(new GetObjectRequest(bucket, fileName));

        //1. base64??? ????????? ???????????? decode?????? ?????? wav????????? ??????
        byte[] decoded1 = Base64.decode(encodedUserVoice);

        File recordFile = null;
        InputStream recordIn = new ByteArrayInputStream(decoded1);
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(rootPath + movePath + "record.wav"));
            dos.write(decoded1);
            AudioFormat format = new AudioFormat(8000f, 16, 1, true, false);
            AudioInputStream stream = new AudioInputStream(recordIn, format, decoded1.length);
            recordFile = new File(rootPath + movePath + "record.wav");
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, recordFile);
        }catch (Exception e){
            e.printStackTrace();
        }

        //2. S3???????????? ?????? ???????????? File??? ??????
        InputStream s3is = savedUserVoice.getObjectContent();
        byte[] decoded2 = IOUtils.toByteArray(s3is);
        File savedFile = null;
        InputStream saveIn = new ByteArrayInputStream(decoded2);
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(rootPath + movePath + "saved.wav"));
            dos.write(decoded2);
            AudioFormat format = new AudioFormat(8000f, 16, 1, true, false);
            AudioInputStream stream = new AudioInputStream(saveIn, format, decoded2.length);
            savedFile = new File(rootPath+movePath+"saved.wav");
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, savedFile);
        }catch (Exception e){
            e.printStackTrace();
        }

        Wave w1 = new Wave(recordFile.getPath());
        Wave w2 = new Wave(savedFile.getPath());

        FingerprintSimilarity fps = w1.getFingerprintSimilarity(w2);
        float fileScore = fps.getScore(); //???????????? ???
        float score = fps.getSimilarity(); //?????????

        // ?????? ??????
        recordFile.delete();
        savedFile.delete();

        return score;
    }

    /* face detection */
    public File decodeImage(String encodedString) throws IOException {
        byte[] decodedBytes = Base64.decode(encodedString);
        String decodedString = new String(decodedBytes);

        // ????????? ???????????? file ????????? ??????

        File inputImage = new File(rootPath+movePath + "img.jpg");

        FileOutputStream fileOutputStream = new FileOutputStream(inputImage);
        fileOutputStream.write(decodedBytes);
        fileOutputStream.close();

        return inputImage;
    }

    public File getFile(String filePath) throws IOException {
        // S3?????? ????????? ??????????????? ????????? amazonS3Client
        AmazonS3 amazonS3Client = amazonS3Client();

        InputStream in = amazonS3Client.getObject(bucket, filePath).getObjectContent();
        File file = File.createTempFile("s3file", "");
        Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return file;
    }


    public float identifyFace(FaceBiometricRequest faceBiometricsRequest) throws Exception {
        Float similarityThreshold = 70F;

        // ??????????????? ????????? [????????? ?????? ????????? ?????????]??? file ????????? ??????
        File inputImage = decodeImage(faceBiometricsRequest.getEncodedUserFace());

        AmazonS3 amazonS3Client = amazonS3Client();
        // DID??? ???????????? [?????? ????????? ????????? ?????????]
        // https://blockai-bucket.s3.ap-northeast-2.amazonaws.com/test.png
        String fileName = faceBiometricsRequest.getSavedS3UserFaceUrl().split("/")[3];
        com.amazonaws.services.s3.model.S3Object savedUserImage = amazonS3Client.getObject(new GetObjectRequest(bucket, fileName));

        ByteBuffer sourceImageBytes = null;
        ByteBuffer targetImageBytes = null;

        //Load source and target images and create input parameters
        try (InputStream inputStream = new FileInputStream(inputImage)) {
            sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            System.out.println("Failed to load source image");
            System.exit(1);
        }
        try (InputStream inputStream = savedUserImage.getObjectContent()) {
            targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            System.out.println("Failed to load target images");
            System.exit(1);
        }

        Image source = new Image()
                .withBytes(sourceImageBytes);
        Image target = new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold);

        // Call operation
        CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);

        float result = 0;

        // Display results
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match : faceDetails) {
            ComparedFace face = match.getFace();
            BoundingBox position = face.getBoundingBox();
            result = match.getSimilarity();
        }

        //?????? ??????
        inputImage.delete();
        return result;
    }

    public String saveFace(String encodedUserFace, String userId) throws IOException {
        // ??????????????? ????????? ??????????????? [????????? ?????? ????????? ?????????]
        File inputImage = decodeImage(encodedUserFace);

        AmazonS3 amazonS3Client = amazonS3Client();
        String fileName = userId; // ????????? ???????????? ??????????
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputImage).withCannedAcl(CannedAccessControlList.PublicRead));
        String uploadImageUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        return uploadImageUrl;
    }

    private boolean isIssuedDid(DID did) {
        return did != null;
    }

    private List<Type> getBiometricsCertificateFromBlockchain(String didAddress) throws IOException {
        List<TypeReference<?>> outputParameters = Arrays.asList(
                new TypeReference<Utf8String>() {
                },
                new TypeReference<Utf8String>() {
                },
                new TypeReference<Uint256>() {
                }
        );

        Function function = new Function("getDID", Arrays.asList(new Address(didAddress)), outputParameters);
        List<Type> ethereumCallResult = ethereumService.ethCall(function);
        return ethereumCallResult;
    }

    public BiometricsCertificateRequest getBiometricsFromBlockchain(int userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        DID did = user.getDid();

        // DID ?????? ?????? ??????
        if (!isIssuedDid(did)) {
            throw new DidNotYetIssuedException();
        }

        String didAddress = did.getDidAddress();
        List<Type> ethereumCallResult = getBiometricsCertificateFromBlockchain(didAddress);

        String faceCertificateFromBlockchain = ethereumService.decode(String.valueOf(ethereumCallResult.get(0).getValue()));
        String voiceCertificateFromBlockchain = ethereumService.decode(String.valueOf(ethereumCallResult.get(1).getValue()));

        BiometricsCertificateRequest res = new BiometricsCertificateRequest(faceCertificateFromBlockchain, voiceCertificateFromBlockchain, "");
        return res;
    }

    public FaceBiometricResponse searchFace(int userId) throws Exception {
        String faceFromBlockchain = getBiometricsFromBlockchain(userId).getFace();
        File imageFile = s3Service.getFile(faceFromBlockchain.split("/")[3]);
        String encoded = Base64Encoder.encodeFileToBase64Binary(imageFile, 0);
        return FaceBiometricResponse.from(encoded);
    }

    public VoiceBiometricResponse searchVoice(int userId) throws Exception {
        String voiceFromBlockchain = getBiometricsFromBlockchain(userId).getVoice();
        File voiceFile = s3Service.getFile(voiceFromBlockchain.split("/")[3]);
        String encoded = Base64Encoder.encodeFileToBase64Binary(voiceFile, 1);
        return VoiceBiometricResponse.from(encoded);
    }

}
