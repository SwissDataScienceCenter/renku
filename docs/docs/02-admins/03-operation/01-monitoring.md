---
title: Monitoring
---

All of the tools described here are not part of a Renku installation.
It is strongly recommended however that administrators who are running
Renku in production should install these in their cluster. You may already
be using different alternatives or you may have some of these pre-installed
in your managed Kubernetes cluster.

## [Prometheus](https://prometheus.io/)

Scrapes metrics and data from services running in your cluster.

## [Grafana](https://grafana.com/)

Visualizes metrics scraped by Prometheus. We can provide some dashboards
upon request that we use in production.

## [Alertmanager](https://github.com/prometheus/alertmanager)

Organizes and routes alerts to different receiver integrations
such as email, PagerDuty, Slack, etc. Please reach out if you want
some examples of the types of alerts we use in production.
