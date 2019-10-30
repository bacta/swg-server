From the docker/postgres directory run:

**Building**

`'docker build -t bacta-postgres .'`

**Running**

`'docker run -it --name bacta-postgres -e POSTGRES_PASSWORD=password -p 5432:5432 bacta-postgres'`

**Stopping**

`'docker stop bacta-postgres'`

**Removing container**

`'docker ps -a'`<br>
`'docker rm <container id>'`