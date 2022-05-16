package com.a506.blockai.api.service;

import com.a506.blockai.config.EthereumProperties;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

@Service
public class EthereumService {

    private final String from;
    private final String contract;
    private final String privateKey;
    private final Admin web3j;
    private final String rsaPrivateKey;
    private final String rsaPublicKey;
    private final RSAService rsaService;

    public EthereumService(EthereumProperties ethereumProperties) {
        this.from = ethereumProperties.getFrom();
        this.contract = ethereumProperties.getContract();
        this.privateKey = ethereumProperties.getPrivateKey();
        this.web3j = Admin.build(new HttpService(ethereumProperties.getNetworkUrl()));
        this.rsaPrivateKey = ethereumProperties.getRSAPrivateKey();
        this.rsaPublicKey = ethereumProperties.getRSAPublicKey();
        this.rsaService = new RSAService();
    }

    public List<Type> ethCall(Function function) throws IOException {
        //1. transaction 제작
        Transaction transaction = Transaction.createEthCallTransaction(from, contract,
                FunctionEncoder.encode(function));
        System.out.println("************* Into ethCall *************");
        System.out.println("EthereumService <ethCall> transaction : " + transaction);
        //2. ethereum 호출후 결과 가져오기
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
        System.out.println("EthereumService <ethCall> ethCall : " + ethCall);
        //3. 결과값 decode
        List<Type> decode = FunctionReturnDecoder.decode(ethCall.getResult(),
                function.getOutputParameters());

        System.out.println("EthereumService <ethCall> ethCall.getResult() : " + ethCall.getResult());
        for (int i = 0; i < decode.size(); i++){
            System.out.println("EthereumService <ethCall> decode result : " + decode.get(i));
        }
        System.out.println("EthereumService <ethCall> decode size : " + decode.size());
        System.out.println("************* return ethCall *************");
        return decode;
    }

    public String ethSendRawTransaction(Function function) throws IOException {
        //nonce 조회
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                from, DefaultBlockParameterName.PENDING).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("EthereumService <ethSendRawTransaction> nonce : "+nonce);

        BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
        BigInteger GAS_LIMIT = BigInteger.valueOf(772197L);

        // 트랜잭션 생성
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, GAS_PRICE,
                GAS_LIMIT, contract, FunctionEncoder.encode(function));
        System.out.println("EthereumService <ethSendRawTransaction> rawTransaction : "+rawTransaction);

        // 트랜잭션 서명
        Credentials credentials = Credentials.create(privateKey);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        System.out.println("EthereumService <ethSendRawTransaction> hexValue : " + hexValue);

        // 트랜잭션 전송
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        System.out.println("EthereumService <ethSendRawTransaction> ethSendTransaction : " + ethSendTransaction);

        if(ethSendTransaction.hasError()) {
            System.out.println("-------Transaction Send Error !!!-------");
            System.out.println("EthereumService <ethSendRawTransaction> Transcation error : " + ethSendTransaction.getError().getMessage());
            System.out.println("-------Transaction Send Error !!!-------");
        }
        String hash = ethSendTransaction.getTransactionHash();
        System.out.println("EthereumService <ethSendRawTransaction> hash : " + hash);
        return hash;
    }

    public String sha256(String msg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg.getBytes());

        return bytesToHex(md.digest());
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        builder.append("0x");
        for (byte b: bytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

    public String encode(String data) {

//        HashMap<String, String> stringKeypair = rsaService.createKeypairAsString();
//
//        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        System.out.println("EthereumService <encode> stringKeypair : "+ stringKeypair.values());
//        System.out.println("EthereumService <encode> rsaPrivateKey : " + rsaPrivateKey);
//        System.out.println("EthereumService <encode> rsaPublicKey : " + rsaPublicKey);

        // 여긴 원래 rsaPublicKey
        return rsaService.encode(data, rsaPublicKey);
//        return rsaService.encode(data, rsaPrivateKey);
    }

    public String decode(String data) {
        // 여긴 원래 rsaPrivateKey
//        return rsaService.decode(data, rsaPublicKey);
        return rsaService.decode(data, rsaPrivateKey);
    }

}