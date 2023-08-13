package jna;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.annotation.processing.Generated;
import java.util.HashMap;
import java.util.Map;

//https://www.jsonschema2pojo.org/
//import javax.annotation.Generated;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "sun.desktop",
        "awt.toolkit",
        "java.specification.version",
        "sun.cpu.isalist",
        "sun.jnu.encoding",
        "java.class.path",
        "java.vm.vendor",
        "sun.arch.data.model",
        "user.variant",
        "java.vendor.url",
        "user.timezone",
        "os.name",
        "java.vm.specification.version",
        "user.country",
        "sun.java.launcher",
        "sun.boot.library.path",
        "sun.java.command",
        "jdk.debug",
        "sun.cpu.endian",
        "user.home",
        "user.language",
        "java.specification.vendor",
        "java.version.date",
        "java.home",
        "file.separator",
        "java.vm.compressedOopsMode",
        "line.separator",
        "java.specification.name",
        "java.vm.specification.vendor",
        "java.awt.graphicsenv",
        "user.script",
        "sun.management.compiler",
        "java.runtime.version",
        "user.name",
        "path.separator",
        "os.version",
        "java.runtime.name",
        "file.encoding",
        "java.vm.name",
        "java.vendor.url.bug",
        "java.io.tmpdir",
        "java.version",
        "user.dir",
        "os.arch",
        "java.vm.specification.name",
        "java.awt.printerjob",
        "sun.os.patch.level",
        "java.library.path",
        "java.vm.info",
        "java.vendor",
        "java.vm.version",
        "sun.io.unicode.encoding",
        "java.class.version"
})

@Generated("jsonschema2pojo")
public class SystemProp {
    @JsonProperty("sun.desktop")
    private String sunDesktop;
    @JsonProperty("awt.toolkit")
    private String awtToolkit;
    @JsonProperty("java.specification.version")
    private String javaSpecificationVersion;
    @JsonProperty("sun.cpu.isalist")
    private String sunCpuIsalist;
    @JsonProperty("sun.jnu.encoding")
    private String sunJnuEncoding;
    @JsonProperty("java.class.path")
    private String javaClassPath;
    @JsonProperty("java.vm.vendor")
    private String javaVmVendor;
    @JsonProperty("sun.arch.data.model")
    private String sunArchDataModel;
    @JsonProperty("user.variant")
    private String userVariant;
    @JsonProperty("java.vendor.url")
    private String javaVendorUrl;
    @JsonProperty("user.timezone")
    private String userTimezone;
    @JsonProperty("os.name")
    private String osName;
    @JsonProperty("java.vm.specification.version")
    private String javaVmSpecificationVersion;
    @JsonProperty("user.country")
    private String userCountry;
    @JsonProperty("sun.java.launcher")
    private String sunJavaLauncher;
    @JsonProperty("sun.boot.library.path")
    private String sunBootLibraryPath;
    @JsonProperty("sun.java.command")
    private String sunJavaCommand;
    @JsonProperty("jdk.debug")
    private String jdkDebug;
    @JsonProperty("sun.cpu.endian")
    private String sunCpuEndian;
    @JsonProperty("user.home")
    private String userHome;
    @JsonProperty("user.language")
    private String userLanguage;
    @JsonProperty("java.specification.vendor")
    private String javaSpecificationVendor;
    @JsonProperty("java.version.date")
    private String javaVersionDate;
    @JsonProperty("java.home")
    private String javaHome;
    @JsonProperty("file.separator")
    private String fileSeparator;
    @JsonProperty("java.vm.compressedOopsMode")
    private String javaVmCompressedOopsMode;
    @JsonProperty("line.separator")
    private String lineSeparator;
    @JsonProperty("java.specification.name")
    private String javaSpecificationName;
    @JsonProperty("java.vm.specification.vendor")
    private String javaVmSpecificationVendor;
    @JsonProperty("java.awt.graphicsenv")
    private String javaAwtGraphicsenv;
    @JsonProperty("user.script")
    private String userScript;
    @JsonProperty("sun.management.compiler")
    private String sunManagementCompiler;
    @JsonProperty("java.runtime.version")
    private String javaRuntimeVersion;
    @JsonProperty("user.name")
    private String userName;
    @JsonProperty("path.separator")
    private String pathSeparator;
    @JsonProperty("os.version")
    private String osVersion;
    @JsonProperty("java.runtime.name")
    private String javaRuntimeName;
    @JsonProperty("file.encoding")
    private String fileEncoding;
    @JsonProperty("java.vm.name")
    private String javaVmName;
    @JsonProperty("java.vendor.url.bug")
    private String javaVendorUrlBug;
    @JsonProperty("java.io.tmpdir")
    private String javaIoTmpdir;
    @JsonProperty("java.version")
    private String javaVersion;
    @JsonProperty("user.dir")
    private String userDir;
    @JsonProperty("os.arch")
    private String osArch;
    @JsonProperty("java.vm.specification.name")
    private String javaVmSpecificationName;
    @JsonProperty("java.awt.printerjob")
    private String javaAwtPrinterjob;
    @JsonProperty("sun.os.patch.level")
    private String sunOsPatchLevel;
    @JsonProperty("java.library.path")
    private String javaLibraryPath;
    @JsonProperty("java.vm.info")
    private String javaVmInfo;
    @JsonProperty("java.vendor")
    private String javaVendor;
    @JsonProperty("java.vm.version")
    private String javaVmVersion;
    @JsonProperty("sun.io.unicode.encoding")
    private String sunIoUnicodeEncoding;
    @JsonProperty("java.class.version")
    private String javaClassVersion;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("sun.desktop")
    public String getSunDesktop() {
        return sunDesktop;
    }

    @JsonProperty("sun.desktop")
    public void setSunDesktop(String sunDesktop) {
        this.sunDesktop = sunDesktop;
    }

    @JsonProperty("awt.toolkit")
    public String getAwtToolkit() {
        return awtToolkit;
    }

    @JsonProperty("awt.toolkit")
    public void setAwtToolkit(String awtToolkit) {
        this.awtToolkit = awtToolkit;
    }

    @JsonProperty("java.specification.version")
    public String getJavaSpecificationVersion() {
        return javaSpecificationVersion;
    }

    @JsonProperty("java.specification.version")
    public void setJavaSpecificationVersion(String javaSpecificationVersion) {
        this.javaSpecificationVersion = javaSpecificationVersion;
    }

    @JsonProperty("sun.cpu.isalist")
    public String getSunCpuIsalist() {
        return sunCpuIsalist;
    }

    @JsonProperty("sun.cpu.isalist")
    public void setSunCpuIsalist(String sunCpuIsalist) {
        this.sunCpuIsalist = sunCpuIsalist;
    }

    @JsonProperty("sun.jnu.encoding")
    public String getSunJnuEncoding() {
        return sunJnuEncoding;
    }

    @JsonProperty("sun.jnu.encoding")
    public void setSunJnuEncoding(String sunJnuEncoding) {
        this.sunJnuEncoding = sunJnuEncoding;
    }

    @JsonProperty("java.class.path")
    public String getJavaClassPath() {
        return javaClassPath;
    }

    @JsonProperty("java.class.path")
    public void setJavaClassPath(String javaClassPath) {
        this.javaClassPath = javaClassPath;
    }

    @JsonProperty("java.vm.vendor")
    public String getJavaVmVendor() {
        return javaVmVendor;
    }

    @JsonProperty("java.vm.vendor")
    public void setJavaVmVendor(String javaVmVendor) {
        this.javaVmVendor = javaVmVendor;
    }

    @JsonProperty("sun.arch.data.model")
    public String getSunArchDataModel() {
        return sunArchDataModel;
    }

    @JsonProperty("sun.arch.data.model")
    public void setSunArchDataModel(String sunArchDataModel) {
        this.sunArchDataModel = sunArchDataModel;
    }

    @JsonProperty("user.variant")
    public String getUserVariant() {
        return userVariant;
    }

    @JsonProperty("user.variant")
    public void setUserVariant(String userVariant) {
        this.userVariant = userVariant;
    }

    @JsonProperty("java.vendor.url")
    public String getJavaVendorUrl() {
        return javaVendorUrl;
    }

    @JsonProperty("java.vendor.url")
    public void setJavaVendorUrl(String javaVendorUrl) {
        this.javaVendorUrl = javaVendorUrl;
    }

    @JsonProperty("user.timezone")
    public String getUserTimezone() {
        return userTimezone;
    }

    @JsonProperty("user.timezone")
    public void setUserTimezone(String userTimezone) {
        this.userTimezone = userTimezone;
    }

    @JsonProperty("os.name")
    public String getOsName() {
        return osName;
    }

    @JsonProperty("os.name")
    public void setOsName(String osName) {
        this.osName = osName;
    }

    @JsonProperty("java.vm.specification.version")
    public String getJavaVmSpecificationVersion() {
        return javaVmSpecificationVersion;
    }

    @JsonProperty("java.vm.specification.version")
    public void setJavaVmSpecificationVersion(String javaVmSpecificationVersion) {
        this.javaVmSpecificationVersion = javaVmSpecificationVersion;
    }

    @JsonProperty("user.country")
    public String getUserCountry() {
        return userCountry;
    }

    @JsonProperty("user.country")
    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    @JsonProperty("sun.java.launcher")
    public String getSunJavaLauncher() {
        return sunJavaLauncher;
    }

    @JsonProperty("sun.java.launcher")
    public void setSunJavaLauncher(String sunJavaLauncher) {
        this.sunJavaLauncher = sunJavaLauncher;
    }

    @JsonProperty("sun.boot.library.path")
    public String getSunBootLibraryPath() {
        return sunBootLibraryPath;
    }

    @JsonProperty("sun.boot.library.path")
    public void setSunBootLibraryPath(String sunBootLibraryPath) {
        this.sunBootLibraryPath = sunBootLibraryPath;
    }

    @JsonProperty("sun.java.command")
    public String getSunJavaCommand() {
        return sunJavaCommand;
    }

    @JsonProperty("sun.java.command")
    public void setSunJavaCommand(String sunJavaCommand) {
        this.sunJavaCommand = sunJavaCommand;
    }

    @JsonProperty("jdk.debug")
    public String getJdkDebug() {
        return jdkDebug;
    }

    @JsonProperty("jdk.debug")
    public void setJdkDebug(String jdkDebug) {
        this.jdkDebug = jdkDebug;
    }

    @JsonProperty("sun.cpu.endian")
    public String getSunCpuEndian() {
        return sunCpuEndian;
    }

    @JsonProperty("sun.cpu.endian")
    public void setSunCpuEndian(String sunCpuEndian) {
        this.sunCpuEndian = sunCpuEndian;
    }

    @JsonProperty("user.home")
    public String getUserHome() {
        return userHome;
    }

    @JsonProperty("user.home")
    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }

    @JsonProperty("user.language")
    public String getUserLanguage() {
        return userLanguage;
    }

    @JsonProperty("user.language")
    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    @JsonProperty("java.specification.vendor")
    public String getJavaSpecificationVendor() {
        return javaSpecificationVendor;
    }

    @JsonProperty("java.specification.vendor")
    public void setJavaSpecificationVendor(String javaSpecificationVendor) {
        this.javaSpecificationVendor = javaSpecificationVendor;
    }

    @JsonProperty("java.version.date")
    public String getJavaVersionDate() {
        return javaVersionDate;
    }

    @JsonProperty("java.version.date")
    public void setJavaVersionDate(String javaVersionDate) {
        this.javaVersionDate = javaVersionDate;
    }

    @JsonProperty("java.home")
    public String getJavaHome() {
        return javaHome;
    }

    @JsonProperty("java.home")
    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    @JsonProperty("file.separator")
    public String getFileSeparator() {
        return fileSeparator;
    }

    @JsonProperty("file.separator")
    public void setFileSeparator(String fileSeparator) {
        this.fileSeparator = fileSeparator;
    }

    @JsonProperty("java.vm.compressedOopsMode")
    public String getJavaVmCompressedOopsMode() {
        return javaVmCompressedOopsMode;
    }

    @JsonProperty("java.vm.compressedOopsMode")
    public void setJavaVmCompressedOopsMode(String javaVmCompressedOopsMode) {
        this.javaVmCompressedOopsMode = javaVmCompressedOopsMode;
    }

    @JsonProperty("line.separator")
    public String getLineSeparator() {
        return lineSeparator;
    }

    @JsonProperty("line.separator")
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    @JsonProperty("java.specification.name")
    public String getJavaSpecificationName() {
        return javaSpecificationName;
    }

    @JsonProperty("java.specification.name")
    public void setJavaSpecificationName(String javaSpecificationName) {
        this.javaSpecificationName = javaSpecificationName;
    }

    @JsonProperty("java.vm.specification.vendor")
    public String getJavaVmSpecificationVendor() {
        return javaVmSpecificationVendor;
    }

    @JsonProperty("java.vm.specification.vendor")
    public void setJavaVmSpecificationVendor(String javaVmSpecificationVendor) {
        this.javaVmSpecificationVendor = javaVmSpecificationVendor;
    }

    @JsonProperty("java.awt.graphicsenv")
    public String getJavaAwtGraphicsenv() {
        return javaAwtGraphicsenv;
    }

    @JsonProperty("java.awt.graphicsenv")
    public void setJavaAwtGraphicsenv(String javaAwtGraphicsenv) {
        this.javaAwtGraphicsenv = javaAwtGraphicsenv;
    }

    @JsonProperty("user.script")
    public String getUserScript() {
        return userScript;
    }

    @JsonProperty("user.script")
    public void setUserScript(String userScript) {
        this.userScript = userScript;
    }

    @JsonProperty("sun.management.compiler")
    public String getSunManagementCompiler() {
        return sunManagementCompiler;
    }

    @JsonProperty("sun.management.compiler")
    public void setSunManagementCompiler(String sunManagementCompiler) {
        this.sunManagementCompiler = sunManagementCompiler;
    }

    @JsonProperty("java.runtime.version")
    public String getJavaRuntimeVersion() {
        return javaRuntimeVersion;
    }

    @JsonProperty("java.runtime.version")
    public void setJavaRuntimeVersion(String javaRuntimeVersion) {
        this.javaRuntimeVersion = javaRuntimeVersion;
    }

    @JsonProperty("user.name")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("user.name")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("path.separator")
    public String getPathSeparator() {
        return pathSeparator;
    }

    @JsonProperty("path.separator")
    public void setPathSeparator(String pathSeparator) {
        this.pathSeparator = pathSeparator;
    }

    @JsonProperty("os.version")
    public String getOsVersion() {
        return osVersion;
    }

    @JsonProperty("os.version")
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    @JsonProperty("java.runtime.name")
    public String getJavaRuntimeName() {
        return javaRuntimeName;
    }

    @JsonProperty("java.runtime.name")
    public void setJavaRuntimeName(String javaRuntimeName) {
        this.javaRuntimeName = javaRuntimeName;
    }

    @JsonProperty("file.encoding")
    public String getFileEncoding() {
        return fileEncoding;
    }

    @JsonProperty("file.encoding")
    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    @JsonProperty("java.vm.name")
    public String getJavaVmName() {
        return javaVmName;
    }

    @JsonProperty("java.vm.name")
    public void setJavaVmName(String javaVmName) {
        this.javaVmName = javaVmName;
    }

    @JsonProperty("java.vendor.url.bug")
    public String getJavaVendorUrlBug() {
        return javaVendorUrlBug;
    }

    @JsonProperty("java.vendor.url.bug")
    public void setJavaVendorUrlBug(String javaVendorUrlBug) {
        this.javaVendorUrlBug = javaVendorUrlBug;
    }

    @JsonProperty("java.io.tmpdir")
    public String getJavaIoTmpdir() {
        return javaIoTmpdir;
    }

    @JsonProperty("java.io.tmpdir")
    public void setJavaIoTmpdir(String javaIoTmpdir) {
        this.javaIoTmpdir = javaIoTmpdir;
    }

    @JsonProperty("java.version")
    public String getJavaVersion() {
        return javaVersion;
    }

    @JsonProperty("java.version")
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    @JsonProperty("user.dir")
    public String getUserDir() {
        return userDir;
    }

    @JsonProperty("user.dir")
    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    @JsonProperty("os.arch")
    public String getOsArch() {
        return osArch;
    }

    @JsonProperty("os.arch")
    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    @JsonProperty("java.vm.specification.name")
    public String getJavaVmSpecificationName() {
        return javaVmSpecificationName;
    }

    @JsonProperty("java.vm.specification.name")
    public void setJavaVmSpecificationName(String javaVmSpecificationName) {
        this.javaVmSpecificationName = javaVmSpecificationName;
    }

    @JsonProperty("java.awt.printerjob")
    public String getJavaAwtPrinterjob() {
        return javaAwtPrinterjob;
    }

    @JsonProperty("java.awt.printerjob")
    public void setJavaAwtPrinterjob(String javaAwtPrinterjob) {
        this.javaAwtPrinterjob = javaAwtPrinterjob;
    }

    @JsonProperty("sun.os.patch.level")
    public String getSunOsPatchLevel() {
        return sunOsPatchLevel;
    }

    @JsonProperty("sun.os.patch.level")
    public void setSunOsPatchLevel(String sunOsPatchLevel) {
        this.sunOsPatchLevel = sunOsPatchLevel;
    }

    @JsonProperty("java.library.path")
    public String getJavaLibraryPath() {
        return javaLibraryPath;
    }

    @JsonProperty("java.library.path")
    public void setJavaLibraryPath(String javaLibraryPath) {
        this.javaLibraryPath = javaLibraryPath;
    }

    @JsonProperty("java.vm.info")
    public String getJavaVmInfo() {
        return javaVmInfo;
    }

    @JsonProperty("java.vm.info")
    public void setJavaVmInfo(String javaVmInfo) {
        this.javaVmInfo = javaVmInfo;
    }

    @JsonProperty("java.vendor")
    public String getJavaVendor() {
        return javaVendor;
    }

    @JsonProperty("java.vendor")
    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    @JsonProperty("java.vm.version")
    public String getJavaVmVersion() {
        return javaVmVersion;
    }

    @JsonProperty("java.vm.version")
    public void setJavaVmVersion(String javaVmVersion) {
        this.javaVmVersion = javaVmVersion;
    }

    @JsonProperty("sun.io.unicode.encoding")
    public String getSunIoUnicodeEncoding() {
        return sunIoUnicodeEncoding;
    }

    @JsonProperty("sun.io.unicode.encoding")
    public void setSunIoUnicodeEncoding(String sunIoUnicodeEncoding) {
        this.sunIoUnicodeEncoding = sunIoUnicodeEncoding;
    }

    @JsonProperty("java.class.version")
    public String getJavaClassVersion() {
        return javaClassVersion;
    }

    @JsonProperty("java.class.version")
    public void setJavaClassVersion(String javaClassVersion) {
        this.javaClassVersion = javaClassVersion;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}