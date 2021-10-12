package com.fx.dense.utils;
import com.fx.dense.exception.BusinessException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @program: pkc
 * @description: 生成文件信息
 * @author: WangHaiMing
 * @create: 2021-10-08 16:00
 **/
public class GenerateFileUtil {

    /**
     * 生成本地文件
     * @param type 是否需要将内容加密
     * @param context 需要写入的内容
     */
    public static void geFile(boolean type,Object context){
        //  1.1 创建输出流
        FileOutputStream fos = null;

        try{
            /** 在本地新建一个文件夹  里面创建一个文件  向里面写入内容 */
            //1. 文件夹的路径  文件名
           // String directory = "C:\\HamTools";
            String directory = "C:\\";
            String filename = "加密工具常用串.txt";

            File file = new File(directory);
            if (!file.exists()) {
                file.mkdirs();
            }
            File file2 = new File(directory, filename);
            if (!file2.exists()) {
                try {
                    file2.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //3.写入数据
            //创建文件字节输出流
            fos = new FileOutputStream(directory + "\\" + filename);
            //开始写
            String str = String.valueOf(context);
            byte[] bytes = str.getBytes();
            //将byte数组中的所有数据全部写入
            fos.write(bytes);

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
            throw new BusinessException("文件生成失败");
        }finally {
            try{
                //关闭流
                fos.close();
            }catch (Exception e){
                e.printStackTrace();
                e.getCause();
                throw new BusinessException("文件流关闭失败");
            }
        }
    }





}
