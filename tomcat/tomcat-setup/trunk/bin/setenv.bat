set CATALINA_HOME=C:\usr\local\apache-tomcat-6.0.20
set CATALINA_BASE=C:\usr\local\tomcat-instance-1

rem SIZE JVM
set CATALINA_OPTS=%CATALINA_OPTS% -Xmx512m

rem ENABLE JMX
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
    