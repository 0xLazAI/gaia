package com.lazai.utils;

import com.lazai.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

public class EthereumAuthUtils {

    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERROR_LOG");


    public static boolean verifySignature(String message, String signature, String expectedAddress){
        // 计算消息哈希
        byte[] messageHash = Sign.getEthereumMessageHash(message.getBytes());

        // 解析签名
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        Sign.SignatureData signatureData = new Sign.SignatureData(
                signatureBytes[64],
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64)
        );

        // 通过签名恢复公钥
        String recoveredAddress = "";
        try{
            BigInteger publicKey = Sign.signedMessageHashToKey(messageHash, signatureData);
            recoveredAddress = "0x" + Keys.getAddress(publicKey);
        }catch (Throwable e){
            ERROR_LOGGER.error("Signed failed",e);
            throw new DomainException("Signed failed",500);
        }

        // 检查恢复出的地址是否匹配用户提供的地址
        return recoveredAddress.equalsIgnoreCase(expectedAddress);
    }

}
