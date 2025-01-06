FROM eclipse-temurin:21-noble

COPY . .

ADD https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/132/downloads/paper-1.21.1-132.jar server.jar
ADD https://download.luckperms.net/1568/bukkit/loader/LuckPerms-Bukkit-5.4.151.jar plugins/LuckPerms.jar

CMD [ "java", "-jar", "server.jar" ]