#!/bin/ksh
MA_HOME=/home2/csr2/JMSSlowdown
JAVA_HOME=/home2/csr2/Oracle/Middleware/jrockit-jdk1.6.0_20-R28.1.0-4.0.1

MA_LIB=$MA_HOME/lib
CLS_PATH=$MA_LIB\\JMSSlowdown.jar:$MA_LIB\\ICSServices.jar:$MA_LIB\\wlfullclient.jar:$MA_LIB\\activemq-all-4.1.2.jar

$JAVA_HOME/bin/java -cp $CLS_PATH jms.client.JMSLoader

#$JAVA_HOME/bin/java -cp $CLS_PATH jms.client.JMSReadManager
