# Chat

## Клиент серверное приложение для обмена сообщениями

### Теги версий:

<details>
<summary><span style="font-size: medium;"> v1.0</summary>
Клиент и сервер реализованы с использованием java 8 без фреймворков на IO сокетах.
</details>

<details>
<summary><span style="font-size: medium;"> v2.0.0</summary>
<ol>  </ol>
<p style="text-align: left;"><strong>Изменения:</strong></p>
<ol>
<li> Переход на Java 17.</li>
<li style="text-align: left;">Клиент и сервер реализованы на netty.</li>
<li> Добавлена система сборки Maven</li>
<li> Настройки подключения вынесены в application.properties</li>
<li> Структура проекта разделена на 3 модуля: </li>
<ul>
<li>Server</li>
<li>Client</li>
<li>Common - общие ресурсы необходимые для работы основных модулей (включен в клиент и сервер как зависимость для сборки) </li>
</ul>
<li> Добавлен логгер Logback</li>
</ol>

<p style="text-align: left;"><strong>Сборка и запуск:</strong></p>

<ol>
<li>Настройки проекта application.properties</li>
<ul>
<li>server.port - порт на котором работает сервер</li>
<li>server.host - ip адрес сервера</li>
<li>server.users - путь по которому будет проводится сохранение пользователей в файл </li>
</ul>
<li><code>mnv clean package</code> - сборка проекта</li>
<li><code>java -jar Server/target/Server-jar-with-dependencies.jar</code> - запуск сервера</li>
<li><code>java -jar Client/target/Client-jar-with-dependencies.jar</code> - запуск клиента</li>
</ol>

</details>
