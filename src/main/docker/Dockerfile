FROM registry.cn-beijing.aliyuncs.com/yilihuo/java_alpine_jre:8_121

MAINTAINER lyzhang "zhangluyan@jsh.com"

ENV APP_NAME dingtalk-0.0.1-SNAPSHOT.jar
ENV WORK_PATH /data/dingtalk/

#VOLUME
VOLUME ["/data/logs", "/tmp/data"]

ADD $APP_NAME $WORK_PATH
#COPY . $WORK_PATH/
WORKDIR $WORK_PATH
# ENTRYPOINT
ENTRYPOINT ["java","-jar","$APP_NAME"]

# CMD
#CMD ["-jar", "$ARTIFACTID-$ARTIFACTVERSION.jar"]
