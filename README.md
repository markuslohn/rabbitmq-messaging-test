# RabbitMQ-Test



Im Rahmen eines anstehenden Projektes wollte ich die grundsätzliche Architektur und Konzepte von RabbitMQ verstehen und ausprobieren. Ferner wollte ich analysieren, wo die Unterschiede zu anderen Message Brokern liegen.

**Hinweis:** Alle Schritte habe ich auf einem MacBook Pro mit M2 Prozessor ausgeführt. Möglicherweise sind einige Schritte aber auf einer anderen Hardware nicht erforderlich!

Insgesamt wird für diesen Test eine RabbitMQ-Instanz benötigt. Zusätzlich gibt es zwei Projekte Quarkus-Projekte, die zum einen Nachrichten in die Queue legen und zum anderen Nachrichten aus der Queue lesen. Das folgende Schaubild veranschaulicht den Anwendungsfall:

![](https://quarkus.io/guides/images/amqp-qs-architecture.png)



## Installationsschritte

### Installation von RabbitMQ

Die Installation von RabbitMQ läuft mit einer einfachen Installation auf Basis eines Containers in einer Podman Runtime.

#### Installation mit Container ohne Management Interface (optional - besser mit Management Interface)

1. Image laden

```bash
podman pull docker.io/library/rabbitmq:latest
```

2. Container im Hintergrund starten

```bash
podman run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_ERLANG_COOKIE="rabbitmqcookie" -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -e RABBITMQ_NODENAME=rabbit1@rabbitmq rabbitmq:latest
```



#### Installation mit Container und Management Interface

1. Image laden

```bash
podman pull docker.io/library/rabbitmq:management
```

2. Container im Hintergrund starten

```bash
podman run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_ERLANG_COOKIE="rabbitmqcookie" -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -e RABBITMQ_NODENAME=rabbit1@rabbitmq rabbitmq:management
```

3. Management Console im Webbrowser aufrufen

​	Mit der URL http://localhost:15672 kann die Console im Browser geöffnet werden:

​	Anmeldedaten: admin/admin



### Konfiguration von RabbitMQ

1. Mit Shell im Container anmelden:

```bash
podman  exec -it rabbitmq bash
```

2. AMQP 1.0 Plugin aktivieren

```bash
rabbitmq-plugins enable rabbitmq_amqp1_0
```

Die installierten Plugins können unter /etc/rabbitmq/enabled_plugins eingesehen werden. Ggf. muss ein Neustart des Containers durchgeführt werden.

3. Benutzer anlegen

```bash
rabbitmqctl add_user vcsdev vcsdev
rabbitmqctl set_user_tags vcsdev administrator
rabbitmqctl set_permissions -p / vcsdev ".*" ".*" ".*"
```

Benutzername/Passwort kann mit dem Kommando `rabbitmqctl authenticate_user vcsdev vcsdev` geprüft werden. Die eingerichteten Permissions können dann mit dem Kommando `rabbitmqctl list_permissions --vhost /` geprüft werden.



**Weitere nützliche Kommandos:**

```bash
rabbitmqctl list_queues
```



### IP-Adresse des RabbitMQ-Containers ermitteln

Damit später die Kommunikation zwischen den Containern funktionieren kann, muss die IP-Adresse des RabbitMQ-Containers ermitteln werden. Diese IP-Adresse wird dann später in den Quarkus-Projekten benötigt:

```bash
podman container inspect -f '{{.NetworkSettings.IPAddress}}' rabbitmq
```

=> Ergebnis: 10.88.0.2



### rabbitmq-quickstart-producer konfigurieren

In der Datei src/main/resources/application.properties den folgenden Abschnitt prüfen und ggf. die IP-Adresse aus dem Schritt zuvor eintragen:

```bash
rabbitmq-host=10.88.0.2
rabbitmq-port=5672 
rabbitmq-username=vcsdev
rabbitmq-password=vcsdev
```

Danach die Applikaton mit dem Kommando. Die Ausführung muss im Root-Verzeichnis der Applikation erfolgen. Die Applikation wird auf den Port 8080 gestartet. Bei Bedarf den Port ändern (application.properties).

```bash
mvn quarkus:dev
```

starten.

### rabbitmq-quickstart-processor konfigurieren

In der Datei src/main/resources/application.properties den folgenden Abschnitt prüfen und ggf. die IP-Adresse aus dem Schritt zuvor eintragen:

```bash
rabbitmq-host=10.88.0.2
rabbitmq-port=5672 
rabbitmq-username=vcsdev
rabbitmq-password=vcsdev
```

Danach die Applikaton mit dem Kommando. Die Ausführung muss im Root-Verzeichnis der Applikation erfolgen. Die Applikation wird auf den Port 8085 gestartet. Bei Bedarf den Port ändern (application.properties).

```bash
mvn quarkus:dev
```

starten.

**Hinweis:**

Damit sind alle Installations und Konfigurationsschritte abgeschlossen. Die Queues werden automatisch durch die Applikation angelegt.

Weiter geht's dann mit "Anwendungsfall ausprobieren"

## Anwendungsfall ausprobieren

1. Im Webbrowser die URL http://localhost:8080/quotes.html aufrufen und über den Button <Request Quote> Anfragen erzeugen.
2. Im Logfile der Applikation rabbitmq-quickstart-processor prüfen, dass Nachrichten gelesen wurden.



## Werkzeuge

**Rabbitrace -> VSCode Plugin für RabbitMQ**

Download unter https://marketplace.visualstudio.com/items?itemName=rohinivsenthil.rabbitrace

Installation mit folgendem Kommando:

```bash
/opt/openvscode/bin/openvscode-server --install-extension rohinivsenthil.rabbitrace-1.0.1.vsix 
```



## Ressources

- [Rapid bootstrap of RabbitMQ in a container](https://radeksm.github.io/2020/02/08/RabbitMQ_bootstrap_in_container.html)
- [Getting started to Quarkus messaging with AMQP 1.0](https://quarkus.io/guides/amqp)
- [Getting started to Quarkus messaging with RabbitMQ](https://quarkus.io/guides/rabbitmq)
- [RabbitMQ Performance Testing Tool](https://github.com/rabbitmq/rabbitmq-perf-test/)

