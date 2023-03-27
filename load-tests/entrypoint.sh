k6 run --include-system-env-vars -o experimental-prometheus-rw scenarioCoreRequests.js
k6 run --include-system-env-vars -o experimental-prometheus-rw scenarioFileUpload.js
k6 run --include-system-env-vars -o experimental-prometheus-rw scenarioLecture.js
k6 run --include-system-env-vars -o experimental-prometheus-rw scenarioProjectMigration.js
