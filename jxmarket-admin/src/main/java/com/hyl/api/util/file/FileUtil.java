package com.hyl.api.util.file;

import com.google.common.collect.Maps;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;


/**
 * word 工具类
 * @author AnneHan
 * @date 2023-09-15
 */
@Slf4j
public class FileUtil {

    private static final String UNIT_B = "B";
    private static final String UNIT_K = "k";
    private static final String UNIT_M = "M";
    private static final String UNIT_G = "G";

    private static final Long B_SIZE = 1024L;
    private static final Long K_SIZE = 1048576L;
    private static final Long M_SIZE = 1073741824L;

    private static final int BUFFER_SIZE = 1024;

    /**
     * 默认大小 10M
     */
    public static final long DEFAULT_MAX_SIZE = 10 * 1024 * 1024;


    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * BASE64解码成File文件
     *
     * @param destPath 生成目录地址
     * @param base64   base加密字符串
     * @param fileName 文件名称
     */
    public static void base64ToFile(String destPath, String base64, String fileName) {
        log.info("Base 64转码文件");
        File file = null;
        //创建文件目录
        FileUtil.mkdirFile(destPath);
        byte[] bytes = Base64.getDecoder().decode(base64);
        file = new File(destPath + "/" + fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(bytes);
        } catch (Exception e) {
            log.info("Base64解析生成文件失败:{}", e.getMessage());
        }
    }

    /**
     * 校验文件大小
     *
     * @param len   文件大小
     * @param start 大小范围
     * @param end   大小范围
     * @param unit  比较单位
     * @return 比较结果
     */
    public static boolean checkFileSize(Long len, Long start, Long end, String unit) {
        double fileSize = 0;
        if (UNIT_B.equalsIgnoreCase(unit)) {
            fileSize = (double) len;
        } else if (UNIT_K.equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1024;
        } else if (UNIT_M.equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1048576;
        } else if (UNIT_G.equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1073741824;
        }
        return fileSize >= start && fileSize <= end;
    }


    public static String formatFileSize(Long size) {
        if (size <= 0) {
            return 0 + UNIT_B;
        }
        double fileSize;
        if (size < B_SIZE) {
            fileSize = (double) size;
            return fileSize + UNIT_B;
        } else if (size < K_SIZE) {
            fileSize = (double) size / 1024;
            return fileSize + UNIT_K;
        } else if (size < M_SIZE) {
            fileSize = (double) size / 1048576;
            return fileSize + UNIT_M;
        } else {
            fileSize = (double) size / 1073741824;
            return fileSize + UNIT_G;
        }
    }


    /**
     * 文件下载
     *
     * @param url          远程文件路径
     * @param generatePath 本地生成路径
     * @param fileName     文件名称
     */
    public static boolean download(String url, String generatePath, String fileName, RestTemplate restTemplate) {
        FileUtil.mkdirFile(generatePath);
        File file = new File(generatePath + "/" + fileName);
        try (FileOutputStream fos = new FileOutputStream(file);) {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Resource> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, byte[].class);
            log.info("===状态码================");
            log.info(">> {}", response.getStatusCodeValue());
            log.info("===返回信息================");
            log.info(">> {}", response.getHeaders().getContentType());
            log.info(">> {}", Objects.requireNonNull(response.getHeaders().getContentType()).getSubtype());
            fos.write(Objects.requireNonNull(response.getBody()));
            fos.flush();
        } catch (Exception e) {
            log.error("远程文件{}下载失败：{}", fileName, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 判断目标文件夹是否存在，不存在则生成
     *
     * @param filePath
     */
    public static void mkdirFile(String filePath) {
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            boolean flg = dir.mkdirs();
            if (flg) {
                log.info("目录:{}创建成功", filePath);
            } else {
                log.info("目录:{}创建失败", filePath);
            }
        }
    }

    /**
     * 删除文件夹
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deletePrefixFileDirectory(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径不为空则进行删除
        if (file.isDirectory() && file.exists()) {
            flag = FileUtils.deleteQuietly(file);
        }
        return flag;
    }

    /**
     * 根据源文件压缩图片
     *
     * @param fileSize   文件大小
     * @param localFile  本地文件
     * @param simpleFile 压缩文件
     * @throws IOException exception
     */
    public static void compressImageFileBySize(Long fileSize, File localFile, File simpleFile) throws IOException {
        //scale 图片缩小倍数(0~1),1代表保持原有的大小
        //outputQuality 压缩的质量,1代表保持原有的大小(默认1)
        double scale;
        double outputQuality;
        if (checkFileSize(fileSize, 1L, 100L, "K")) {
            outputQuality = 1f;
            scale = 1f;
        } else if (checkFileSize(fileSize, 101L, 500L, "K")) {
            outputQuality = 0.8f;
            scale = 0.8f;
        } else if (checkFileSize(fileSize, 501L, 1000L, "K")) {
            outputQuality = 0.6f;
            scale = 0.5f;
        } else if (checkFileSize(fileSize, 1001L, 3000L, "K")) {
            outputQuality = 0.5f;
            scale = 0.3f;
        } else {
            outputQuality = 0.5f;
            scale = 0.2f;
        }
        Thumbnails.of(localFile).scale(scale).outputQuality(outputQuality).toFile(simpleFile);
    }

    /**
     * 判断文件大小
     *
     * @param len  文件长度
     * @param size 限制大小
     * @param unit 限制单位（B,K,M,G）
     * @return
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
//        long len = file.length();
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }

    /**
     * 过滤符合条件的文件名称
     *
     * @param localFilePath
     * @param prefixs
     * @return
     */
    public static File findPrefixFile(String localFilePath, String[] prefixs) {
        File targetDir = new File(localFilePath);
        if (targetDir.exists() && targetDir.isDirectory()) {
            PrefixFileFilter prefixFileFilter;
            if (prefixs != null) {
                prefixFileFilter = new PrefixFileFilter(prefixs);
            } else {
                prefixFileFilter = new PrefixFileFilter("");
            }
            /**
             * targetDir：不要为 null、不要是文件、不要不存在
             * 第二个参数 文件过滤
             *      1）PrefixFileFilter：为文件名前缀过滤器
             *      2）PrefixFileFilter 构造器参数可以是 String、List<String>、String[] 等
             *      3）如果参数为空，则表示不进行过滤，等同于 TrueFileFilter.INSTANCE
             *
             * 第三个参数 目录过滤
             *      TrueFileFilter.INSTANCE：表示迭代获取所有子孙目录
             *      FalseFileFilter.FALSE：表示只获取目标目录下一级，不进行迭代
             */
            Collection<File> fileCollection = FileUtils.listFiles(targetDir, prefixFileFilter, FalseFileFilter.FALSE);
            if (fileCollection.iterator().hasNext()) {
                return fileCollection.iterator().next();
            }

        }
        return null;
    }

    /**
     * 删除文件目录中已fileName为前缀的文件
     *
     * @param localFilePath  文件路径
     * @param fileNameSuffix 要删除的文件的前缀
     */
    public static void delPrefixFile(String localFilePath, String fileNameSuffix) {
        File folder = new File(localFilePath);
        boolean bol;
        if (folder.isDirectory() && folder.exists()) {
            String[] fileNameArr = folder.list();
            if (null != fileNameArr && fileNameArr.length > 0) {
                for (String fileName : fileNameArr) {
                    bol = fileName.contains(fileNameSuffix);
                    if (bol) {
                        File file = new File(localFilePath + fileName);
                        if (file.delete()) {
                            log.info("文件{}删除成功", fileName);
                        } else {
                            log.info("文件{}删除失败", fileName);
                        }
                    }
                }
            }
        }
    }


    /**
     * 查找文件目录下文件是否包含在list中的文件名称，如果不存在，则删除该文件（表示核心更新了新文件）
     *
     * @param localFilePath 目标目录
     * @param fileNameList  核心返回的文件list
     *                      和目录里的list作比较
     */
    public static void delNotContainFile(String localFilePath, List<String> fileNameList) {
        log.info("delNotContainFile---->删除目录下多余的文件");
        File folder = new File(localFilePath);
        List<String> folderFileNameList = null;
        if (folder.isDirectory() && folder.exists()) {
            String[] fileNameArr = folder.list();
            if (null != fileNameArr && fileNameArr.length > 0) {
                folderFileNameList = new ArrayList<>(Arrays.asList(fileNameArr));
            }
        }
        if (CollectionUtils.isNotEmpty(fileNameList) && CollectionUtils.isNotEmpty(folderFileNameList)) {
            delFiles(localFilePath, fileNameList, folderFileNameList);
        }

    }

    /**
     * 批量删除指定目录下
     *
     * @param localFilePath
     * @param fileNameList
     * @param folderFileNameList
     */
    private static void delFiles(String localFilePath, List<String> fileNameList, List<String> folderFileNameList) {
        folderFileNameList.removeAll(fileNameList);
        delFiles(localFilePath, folderFileNameList);
    }

    /**
     * ]
     * 批量删除指定目录下
     *
     * @param localFilePath      目标目录
     * @param folderFileNameList 要删除的文件名称list
     */
    private static void delFiles(String localFilePath, List<String> folderFileNameList) {
        for (String FileName : folderFileNameList) {
            File file = new File(localFilePath + FileName);
            if (file.delete()) {
                log.info("文件{}删除成功", FileName);
            } else {
                log.info("文件{}删除失败", FileName);
            }
        }
    }


    /**
     * 获取文件MD5值
     *
     * @param fileFullPath
     * @return
     */
    public static String getMD5(String fileFullPath) {
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        byte buffer[] = new byte[BUFFER_SIZE];
        int len;
        try (FileInputStream in = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            ;
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            in.close();
            return bigInt.toString(16);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 在目标文件夹中查找包含关键字的文件
     *
     * @param directory
     * @param fileNameSuffix
     * @return
     */
    public static List<String> searchFiles(String directory, String previewPdfPath, String fileNameSuffix) {
        List<String> result = new ArrayList<>();
        File folder = new File(directory);
        String[] list = folder.list();
        if (list != null) {
            //获取排序后的文件列表
            for (int i = 0; i < list.length; i++) {
                if (list[i].contains(fileNameSuffix)) {
                    result.add(list[i]);
                }
            }
            //如果返回的list不为空则排序一波
            if (CollectionUtils.isNotEmpty(result)) {
                result.sort(Comparator.comparing(s -> strSub(s, fileNameSuffix)));
                return result.stream().map(str -> previewPdfPath + str).collect(Collectors.toList());
            }
        }
        return result;
    }

    /**
     * 截取排序
     *
     * @param s1
     * @param fileNameSuffix
     * @return
     */
    public static Integer strSub(String s1, String fileNameSuffix) {
        String str = s1.substring(fileNameSuffix.length(), s1.lastIndexOf("."));
        return Integer.valueOf(str);
    }

    /**
     * 获取文件名称
     *
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return null;
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            String[] suffixs = fileName.split("\\.");
            if (suffixs.length > 1) {
                return "." + suffixs[suffixs.length - 1];
            }
        }
        return null;
    }


    /**
     * 文件上传
     *
     * @param file      上传文件
     * @param path      上传路径
     * @param imageType 指定文件格式
     * @return 返回传的路径
     */
    public static Map<String, Object> upload(MultipartFile file, String path, String[] imageType) {
        String dateTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd");
        StringBuilder filePath = new StringBuilder(path);
        StringBuilder returnPath = new StringBuilder(); //作为返回路径返回出去
        returnPath = returnPath.append("/avatar").append("/").append(dateTime);
        filePath.append(returnPath);
        Map<String, Object> riderInfoMap  = Maps.newHashMap();
        try {
            File dicFile = new File(filePath.toString());
            if (!dicFile.exists()) {
                dicFile.mkdirs();
            }
            File localFile;
            List<File> uploadFiles = new ArrayList<>();
            //获取文件名
            String originName = file.getOriginalFilename();
            //校验文件大小类型
            //MimeTypeUtil.IMAGE_EXTENSION_AVATAR
            assertAllowed(file, imageType);
            //获取文件名
            String fileSuffix = originName.substring(originName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + fileSuffix;
            localFile = new File(filePath + "/" + fileName);
            try {
                // 文件空校验
                if (file.isEmpty()) {
                    throw new HylException(ResponseCodeEnum.SYSTEM_ERROR, "Failed to store empty file " + fileName);
                }
                // 包含相对路径的校验
                if (fileName.contains("..")) {
                    // This is a security check
                    throw new HylException(ResponseCodeEnum.SYSTEM_ERROR,
                            "Cannot store file with relative path outside current directory "
                                    + fileName);
                }
                file.transferTo(localFile);
                uploadFiles.add(localFile);
                long fileSize = localFile.length();
                if (fileSize == 0L) {
                    throw new HylException(ResponseCodeEnum.SYSTEM_ERROR, "The file size is 0kB");
                }
                riderInfoMap.put("riderPath", returnPath + "/" + fileName);
            } catch (Exception e) {
                // 异常发生删除上传文件夹
                for (File key : uploadFiles) {
                    if (key.exists()) {
                        key.delete();
                    }
                }
                log.error("上传[{}]文件失败：{}", fileName, e);
                throw new HylException(ResponseCodeEnum.SYSTEM_ERROR, "Failed to store file " + fileName);
            }
        } catch (HylException e) {
            log.error(e.getMessage());
        }
        return riderInfoMap;
    }

    /**
     * 文件大小，文件类型校验
     *
     * @param file             上传的文件
     * @param allowedExtension 允许的MIME文件类型
     * @throws HylException 统一处理异常
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) throws HylException {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
            throw new HylException(ResponseCodeEnum.SYSTEM_ERROR, "文件上传异常");
        }
        String extension = getExtension(file);
        //根据流获取文件类型
        //String ext = getRealType(file.getInputStream());
        if (allowedExtension != null &&
                !isAllowedExtension(extension, allowedExtension)
            //&& !isAllowedExtension(ext, allowedExtension)
        ) {
            throw new HylException(ResponseCodeEnum.SYSTEM_ERROR, "文件上传异常");
        }
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            //如果没有后缀
            extension = MimeTypeUtil.getExtension(file.getContentType());
        }
        return extension;
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

}
