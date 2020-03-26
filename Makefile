client-run:
	mvn exec:java -pl client -Dexec.mainClass="GoClient" -Dexec.args="localhost 6868"

server-run:
	cd server
	mvn exec:java -pl server -Dexec.mainClass="GoServer" -Dexec.args="6868"

db:
	@echo shutting down docker with database if exists
	docker-compose down
	sleep 3
	@echo raising up docker with database
	docker-compose up -d
	sleep 5
	@echo initialize tables
	psql -h localhost -d db -U rus2m -q < initial.sql