Тестовое задание на должность мобильного разработчика: написать “native” мобильное приложение Android, которое запрашивает у сервера определённое количество координат точек (x, y), а затем отображает полученный ответ в виде таблицы и графика.  
На главном экране имеется блок информационного текста, поле для ввода числа точек и одна кнопка «Поехали». По нажатию на кнопку осуществляется POST запрос на сервер (https://demo.bankplus.ru/mobws/json/pointsList), внутри которого содержится информация о количестве запрашиваемых точек (count) и параметр “version=1.1”. Сервер выдаёт ответ в JSON формате, пример:  
{"result":0,"response":{"points":[{"x":"1.23", "y":"2.44"},{"x":"2.17", "y":"3.66"}]}}  
"result":0  – означает, что запрос обработан без ошибок, -100 – неверные параметры запроса, -1 – например, в случае перезапуска сервера или иных ситуациях.  
При ошибках, в response содержится объект message с текстом причины, он может быть на английском языке, либо на русском (закодированный Base64). Ошибочную ситуацию нужно уметь обрабатывать и выводить всплывающее окно с поясняющим текстом.
  
Если ответ от сервера получен, то на новом экране должна отобразиться таблица с полученными координатами точек. Ниже должен быть график с точками, соединёнными прямыми линиями. Точки на графике должны следовать по возрастанию координаты x.
 
Дополнительно можно осуществить следующие возможности работы с графиком:  
* изменения масштаба пользователем  
* соединение точек не ломаной линией, а сглаженной  
* работа в портретной и ландшафтной ориентации экрана  
* сохранение изображения графика в файл