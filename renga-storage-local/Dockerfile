ARG from_image
FROM $from_image

ARG uid
ARG gid

ENV uid="$uid" gid="$gid"
USER root
RUN addgroup -g $gid daemon2 && \
    adduser -h /sbin -g daemon2 -s /sbin/nologin -G daemon2 -D -u $uid daemon2 && \
    addgroup daemon2 daemon && \
    addgroup daemon2 bin && \
    addgroup daemon2 adm
USER daemon2
