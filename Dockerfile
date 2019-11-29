FROM registry.cn-beijing.aliyuncs.com/yilihuo/java_alpine_jre:8_121

MAINTAINER lyzhang "zhangluyan@jsh.com"

ENV APP_NAME @project.build.finalName@.@project.packaging@
ENV WORK_PATH /data/@project.name@/

#VOLUME
VOLUME ["/data/logs", "/tmp/data"]

ADD $APP_NAME $WORK_PATH
#COPY . $WORK_PATH/
WORKDIR $WORK_PATH
# ENTRYPOINT
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom"]

# CMD
CMD ["-jar", "@project.build.finalName@.@project.packaging@"]
