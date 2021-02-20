FROM java:8
# VOLUME /tmp
# 将targer目录下的jar包复制到docker容器/home/springboot目录下面目录下面
ADD ./doorkeeper-server/target/doorkeeper-server.jar /home/doorkeeper-server.jar
# 执行命令 (用k8s command 启动 或 docker -it 方便配置jvm大小)
# CMD java -jar /home/doorkeeper-server.jar --spring.config.location=/home/volume/application.yml
