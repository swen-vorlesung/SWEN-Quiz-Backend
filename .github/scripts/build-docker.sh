docker build \
--build-arg DB_CONNECTION_STRING=$DB_CONNECTION_STRING \
-t swenquizcontainer.azurecr.io/swen-quiz-backend:latest ../..
