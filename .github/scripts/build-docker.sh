docker build \
--build-arg DB_CONNECTION_STRING=$DB_CONNECTION_STRING \
-t swen-quiz-backend:latest ../..
