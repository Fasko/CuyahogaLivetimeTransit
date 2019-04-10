from bs4 import BeautifulSoup
import requests
file = open('All_Routes')
i = 1
fh = open("Routes_Directions_Stops.txt", 'a')
all_routes = BeautifulSoup(file, 'html.parser')
for route_page_link in all_routes.find_all('a'):
    current_route_page_link = 'http://www.nextconnect.riderta.com/LiveADADepartureTimes' + route_page_link.get('href')
    route_page_response = requests.get(current_route_page_link, timeout=5)
    route_page_content = BeautifulSoup(route_page_response.content, "html.parser")
    for dir_page_link in route_page_content.find_all(attrs={"class": "adalink"}):
        dir_link = 'http://www.nextconnect.riderta.com/LiveADADepartureTimes' + dir_page_link.get('href')
        dir_page_response = requests.get(dir_link, timeout=5)
        dir_page_content = BeautifulSoup(dir_page_response.content, "html.parser")
        for stop_page_link in dir_page_content.find_all(attrs={"class": "adalink"}):
            stop_link = 'http://www.nextconnect.riderta.com/LiveADADepartureTimes' + stop_page_link.get('href')
            stop_page_response = requests.get(dir_link, timeout=5)
            stop_page_content = BeautifulSoup(stop_page_response.content, "html.parser")
            fh.write((route_page_link.get('title')) + "," + dir_page_link.get('title') +  "," + stop_page_link.get('title') +  "," + stop_link + '\n')




