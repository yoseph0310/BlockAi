// SPDX-License-Identifier: MIT
pragma solidity ^0.8.13;
pragma experimental ABIEncoderV2;

import "./Ownable.sol";

contract Blockai is Ownable {

    struct DID {
        string faceFileUrl;
        string voiceNo;
        uint256 expiryDate;
    }

    uint constant availableDays = 365 days;
    uint size = 0;

    mapping (address => DID) DIDs;

    constructor () public {
        owner = msg.sender;
    }

    function addDID(string memory _faceFileUrl, string memory _voiceNo, address _userAddress) public onlyOwner {
        DIDs[_userAddress] = DID(_faceFileUrl, _voiceNo, block.timestamp + availableDays);
        size++;
    }
    
    function getDID(address _userAddress) public view returns(string memory, string memory, uint256) {
        DID memory userDid = DIDs[_userAddress];
        require (block.timestamp <= userDid.expiryDate);
        return (userDid.faceFileUrl, userDid.voiceNo, userDid.expiryDate);
    }

    /**
     * @dev Get ExpiryDate
     * @param _userAddress, _certificateNumber
     */
    function getExpiryDate(address _userAddress) public view returns(uint256) {
        return DIDs[_userAddress].expiryDate;
    }
    
    function getSize() public view returns(uint256) {
        return size;
    }

}
