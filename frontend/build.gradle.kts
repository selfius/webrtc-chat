plugins {
    id("org.siouan.frontend-jdk21") version "10.0.0"
}

frontend {
    nodeVersion.set("24.4.1")
    assembleScript.set("run build")
    checkScript.set("run check")
}