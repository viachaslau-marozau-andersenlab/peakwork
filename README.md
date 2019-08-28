# DEPLOY STEPS:

## Build project:
1. Run gradle wrapper to build project<br/>
For this you can use following command from console:<br/>
`gradlew build` for Linux<br/>
`gradlew.bat build` for Windows<br/>

## Build docker images and push to the repository
1. Build docker images for services<br/>
Run following commands from console:<br/>
`docker build -f ./app-deploy/docker_app-geo-service -t gcr.io/${your.google.cloud.project}/app-geo-service:v1 .`<br/>
`docker build -f ./app-deploy/docker_app-search-service -t gcr.io/${your.google.cloud.project}/app-search-service:v1 .`<br/>
`docker build -f ./app-deploy/docker_app-update-service -t gcr.io/${your.google.cloud.project}/app-update-service:v1 .`<br/>
**Note:** Don't forget to replace ${your.google.cloud.project} by your google cloud project.<br/>
Run commands from project root directory.<br/>
2. Push docker images to repository<br/>
Run following commands from console:<br/>
`gcloud docker -- push gcr.io/${your.google.cloud.project}/app-geo-service:v1`<br/>
`gcloud docker -- push gcr.io/${your.google.cloud.project}/app-search-service:v1`<br/>
`gcloud docker -- push gcr.io/${your.google.cloud.project}/app-update-service:v1`<br/>
**Notes:** You already must have installed and configured Cloud SDK.<br/>
Don't forget to replace ${your.google.cloud.project} by your google cloud project.<br/>
Check that you can see your docker images at google cloud<br/>

## Create kind in Datastore
1. Go to **app-deploy** folder
2. Create kind in google datastore<br/>
Run following commands google from console:<br/>
`gcloud datastore indexes create ./index.yaml`<br/>
**Notes:** You already must have installed and configured Cloud SDK.<br/>
Check that you can see new kind for datastore<br/>

## Deploy applications to existing kubernetes cluster
1. Create secret to connect to Google Datastore Service<br/>
   - Generate hash of your credentials from json file.<br/>
Run following commands from google console:<br/>
`base64 <cred_file_name>.json`<br/>
   - Replace **${enter.app.secret.key.here}** value by generated hash at app-keys.yaml file<br/>
   - Create Secret in Kubernetes cluster.<br/>
Run following commands from console:<br/>
`kubectl create -f app-keys.yaml`<br/>
2. Deploy services into kubernetes<br/>
   - Replace **${your.google.cloud.project}** value by your google cloud project id for following files: app-geo-service.yaml, app-search-service.yaml, app-update-service.yaml.<br/>
   - Deploy services into kubernetes.<br/>
Run following commands from google console:<br/>
`kubectl create -f app-geo-service.yaml`<br/>
`kubectl create -f app-search-service.yaml`<br/>
`kubectl create -f app-update-service.yaml`<br/>
3. Test service instances:<br/>
   3.1 By google console<br/>
      -  Get available pods.<br/>
Run following commands from google console:<br/>
`kubectl describe pods`<br/>
      - Chose pod to test.<br/>
Pods for app-geo-service will starts form app-geo-service-...<br/>
Pods for app-search-service will starts form app-search-service-...<br/>
      - send HTTP query by curl.<br/>
Run following commands from google console:<br/>
`kubectl exec -it <pod_name> curl http://localhost:8080/${query.body}`<br/>
   3.2 By exposing services<br/>
      - Expose services by following commands from console:<br/>
`kubectl expose deployment app-geo-service --type=LoadBalancer --port 80 --target-port 8080`<br/>
`kubectl expose deployment app-search-service --type=LoadBalancer --port 80 --target-port 8080`<br/>
`kubectl expose deployment app-update-service --type=LoadBalancer --port 80 --target-port 8080`<br/>
      - Get external IP for your application by following command from console:<br/>
`kubectl get service`<br/>
      - Call service by external API.<br/>
4. Delete services.<br/>
Run following commands from google console:<br/>
`kubectl delete deployment app-geo-service`<br/>
`kubectl delete deployment app-search-service`<br/>
`kubectl delete deployment app-update-service`<br/>

## Run application locally
1. Run gradle wrapper to build project.<br/>
For this you can use following command from console:<br/>
`gradlew build` for Linux<br/>
`gradlew.bat build` for Windows<br/>
2. Add environment variable **GOOGLE_APPLICATION_CREDENTIALS** with a path to credentials json file. 
3. Run springboot application <br/>
For this you can use following command from console:<br/>
`java -jar app-geo-service-1.0-SNAPSHOT.jar` for app-geo-service<br/>
`java -jar app-search-service-1.0-SNAPSHOT.jar` for app-search-service<br/>
`java -jar app-update-service-1.0-SNAPSHOT.jar` for app-update-service<br/>

# SERVICES RESTFUL API
1. RESTful API for app-geo-service.<br/>
This service provides information about the place by geo coordinate.<br/>
You need specify latitude and longitude for this place.<br/>
Request template:<br/>
http://${host}:${port}/geo-coordinate-info?lat=${latitude}&lon=${longitude}<br/>
**lat** and **lon** are required request parameters.<br/>
Request examples: <br/>
http://localhost:8080/geo-coordinate-info?lat=52.5487429714954&lon=-1.81602098644987
2. RESTful API for app-search-service.<br/>
This service provides information about already seen places by country.<br/>
Also there was implemented possibility to see all details of a single place by place id.<br/>
Request template:<br/>
http://${host}:${port}/search-place?country=${country}<br/>
http://${host}:${port}/search-place/${place.id}<br/>
**country** are required request parameters.<br/>
**WARNING:** Place must be loaded to Datastore by app-geo-service before you can see it by app-search-service.<br/>
Request examples:<br/>
http://localhost:8080/search-place?country=Argentina<br/>
http://localhost:8080/search-place/198193853<br/>
3. Information about app-update-service.<br/>
This service updates the information of retrieved places at regular intervals. By default every 5 minutes.<br/>
This service have no public RESTful API.<br/>

# WHAT MUST BE IMPROVED
1. Must be added unit, integration and behavior tests.
2. Must be refactored and rewritten logging part.
3. Must be rewritten RESTful API documentation. Can be used Swagger for this needs.
4. Add and configure apache kafka cluster for following step.
5. Split app-update-service to 2 separate services: first one read data from datastore and send to kafka cluster, second one read from kafka cluster and update datastore values.
6. Try to find better way to connect to Nominatim service.
7. Add possibility to check if we already have this geo coordinate before we go to third party system.
8. Fix Datastore related issues and review Datastore related code.