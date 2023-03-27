FROM grafana/k6:0.43.1
USER 1000:1000
WORKDIR /app
COPY . .
ENTRYPOINT [ "/bin/sh" ]
CMD [ "entrypoint.sh" ]
