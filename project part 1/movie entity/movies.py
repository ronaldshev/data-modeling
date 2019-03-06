import csv
import json
import time

with open("movieid.csv", 'r', newline='\n', encoding='utf-8') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    outputFile = open('movies.csv', 'w', encoding='utf-8')
    output = csv.writer(outputFile, lineterminator='\n')
    count = 0
    listOfMovies = []
    for row in reader:
        single_json = row[0]
        single_json = single_json.replace("'", '"')
        data = json.loads(single_json)
        for name in data:
            insideArray = False
            del name['id']
            del name['name']
            name['movieid'] = row[1]
            name['movieName'] = row[2]
            name['releaseDate'] = row[3]
            name['rating'] = row[4]
            if count == 0:
                header = name.keys()
                output.writerow(header)
                count += 1
            for movieid in listOfMovies:
                if(movieid == name['movieid']):
                    insideArray = True
            if(insideArray == False):
                listOfMovies.append(name['movieid'])
                output.writerow(name.values())
