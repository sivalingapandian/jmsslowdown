set MA_HOME=C:\\cPandian\\project\\JMSSlowdown
set JAVA_HOME=C:\\Progra~1\\Java\\jrockit-jdk1.6.0_29-R28.1.5-4.0.1

set MA_LIB=%MA_HOME%\\lib
set CLS_PATH=%MA_LIB%\\JMSSlowdown.jar;%MA_LIB%\\ICSServices.jar;%MA_LIB%\\wlfullclient.jar;%MA_LIB%\\activemq-all-4.1.2.jar

%JAVA_HOME%\\bin\\java -cp %CLS_PATH% jms.client.JMSLoader 

REM %JAVA_HOME%\\bin\\java -cp %CLS_PATH% jms.client.JMSReadManager
