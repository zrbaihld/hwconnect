package com.huiwang;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


//签名算法
//
//        签名生成的通用步骤如下：
//        第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），
// 使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
//        特别注意以下重要规则：
//        ◆ 参数名ASCII码从小到大排序（字典序）；
//        ◆ 如果参数的值为空不参与签名；
//        ◆ 参数名区分大小写；
//        ◆ 验证调用返回或主动通知签名时，传送的sign参数不参与签名，将生成的签名与该sign值作校验。
//        ◆ 接口可能增加字段，验证签名时必须支持增加的扩展字段
//        第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。
//        key：为商户API秘钥，由平台提供，请小心保管，防止泄露。
//        举例：
//        假设传送的参数如下：
//        trade_type： NATIVE
//        secondtimestamp： 1482400814378
//        orgno： 1017
//        total_fee： 0.01
//        nonce_str： 92488154
//        body： 好好吃
//        第一步：对参数按照key=value的格式，并按照参数名ASCII字典序排序如下：
//        stringA="body=好好吃&nonce_str=92488154&orgno=1017&secondtimestamp=1482400814378&total_fee=0.01&trade_type=NATIVE";
//        第二步：拼接API密钥：
//        stringSignTemp="stringA&key=b1X4rEcOQ9Ewn12"
//        sign=MD5(stringSignTemp).toUpperCase()="A54393910BBD34B73504C834C5EE494E"
//
//        生成随机数算法
//        API接口协议中包含字段nonce_str，主要保证签名不可预测。我们推荐生成随机数算法如下：调用随机数函数生成，将得到的值转换为字符串。





public class EncryptionUtil {

    public static Map<String,String> encryptionRequest(Map<String,String> request,String key){
        ArrayList<String> keyList=new ArrayList<>();
        for (String s : request.keySet()) {
         if (!TextUtils.isEmpty(request.get(s))){
             keyList.add(s);
         }
        }
        Collections.sort(keyList);
        StringBuilder date=new StringBuilder();
        for (String s : keyList) {
            date.append(String.format("%s=%s&",s,request.get(s)));
        }
        date.append(String.format("key=%s",key));


        String sign=md5(date.toString()).toUpperCase();
        Map<String, String> enRequest = new HashMap<>(request);
        enRequest.put("sign",sign);
        return enRequest;
    }

    public static Map<String,Object> DecryptReponse(Map<String,Object> response){
        Map<String ,Object> enRequest=new HashMap<>();


        return enRequest;
    }

    //写一个md5加密的方法
    public static String md5(String plainText) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(plainText.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}
