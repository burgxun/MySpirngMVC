package burgxun.spring.ioc;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @ClassName ScanPackage
 * @Auther burgxun
 * @Description: 扫码包
 * @Date 2020/05/29 16:15
 **/
public class ScanPackage {


    public static void execute(String basePackageName) {
        /* 替换包名称到实际路径名称  例如 burgxun.spring.controller ==>  burgxun/spring/controller */
        String basePackageDirectoryPath = basePackageName.replaceAll("\\.", "/");

        URL basePackageUrl = Thread.currentThread().getContextClassLoader().getResource("/" + basePackageDirectoryPath);
        String protocol = basePackageUrl.getProtocol();//获取文件目录的协议
        if ("file".equals(protocol)) {
            /*获取包的物理路径*/
            String filePath = null;
            try {
                filePath = URLDecoder.decode(basePackageUrl.getFile(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ScanClassInPackageByFile(basePackageName, filePath);
        } else if ("jar".equals(protocol)) {
            ScanClassInPackageByJAR(basePackageUrl, basePackageDirectoryPath);
        }
    }

    private static void ScanClassInPackageByFile(String packageName, String packagePath) {
        System.out.printf("开始扫描，目录 %s \n", packagePath);
        File dir = new File(packagePath);
        if (dir.exists() == false || dir.isDirectory() == false) {
            System.err.printf("定义包的异常 %s \n", packageName);
            return;
        }

        /*列举出 文件下的 class文件和目录*/
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.isDirectory() || pathname.getName().endsWith(".class"));
            }
        });

        /*循环所有的文件*/
        for (File file : files) {
            if (file.isDirectory()) {
                String nowPackageName = packageName + "." + file.getName();//新的包名称
                String nowPackagePath = file.getAbsolutePath();//新的包路径
                ScanClassInPackageByFile(nowPackageName, nowPackagePath);
            } else {
                /* test.class 转化成 test*/
                String className = file.getName().substring(0, file.getName().length() - 6);
                /* 转化为  burgxun.spring.test*/
                String packageClassName = packageName + "." + className;
                try {
                    Class<?> nowClass = Thread.currentThread().getContextClassLoader().loadClass(packageClassName);
                    Container.classs.add(nowClass);//把加载好的类放入 IOC 容器中
                } catch (ClassNotFoundException e) {
                    System.err.printf("加载包%s的时候发生错误\n", packageClassName);
                    e.printStackTrace();
                }
            }
        }
    }

    private static void ScanClassInPackageByJAR(URL url, String basePackageDirectoryPath) {
        System.out.printf("开始JAR包扫描，目录%s \n", basePackageDirectoryPath);
        try {
            /*获取Jar 文件*/
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName(); // name---> /burgxun/spring/controller/test.class
                if (name.endsWith(".class") && !jarEntry.isDirectory()) {
                    if (name.charAt(0) == '/') {
                        name = name.substring(1);// name---> burgxun/spring/controller/test.class
                    }
                    if (name.startsWith(basePackageDirectoryPath)) {
                        int lastIndex = name.lastIndexOf('/');
                        if (lastIndex > -1) {
                            /*路径替换成. packageName-->burgxun.spring.controller   */
                            String packageName = name.substring(0, lastIndex).replace('/', '.');
                            /*去掉 最后的 .class  +1 是因为有/的存在  className--> test*/
                            String className = name.substring(packageName.length() + 1, name.length() - 6);
                            try {
                                Class<?> nowClass = Class.forName(className);
                                Container.classs.add(nowClass);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("在扫描用户jar包文件异常");
            e.printStackTrace();
        }
    }
}
