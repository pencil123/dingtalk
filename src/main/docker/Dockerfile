FROM java

MAINTAINER lyzhang "zhangluyan@jsh.com"

ENV APP_NAME dingtalk-0.0.1-SNAPSHOT.jar
ENV WORK_PATH /

#VOLUME
VOLUME ["/data/logs", "/tmp/data"]

ADD $APP_NAME $WORK_PATH
#COPY . $WORK_PATH/
WORKDIR $WORK_PATH
# ENTRYPOINT
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom"]

# CMD
CMD ["-jar", "/data/dingtalk/dingtalk-0.0.1-SNAPSHOT.jar"]
