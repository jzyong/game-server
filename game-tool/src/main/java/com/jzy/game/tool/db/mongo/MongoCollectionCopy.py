#mongodb 数据库表复制，将一个表数据复制掉另外一个库
#coding：utf-8
from pymongo import MongoClient

__author__ = 'JiangZhiYong'

#mongodb 数据库地址
#fromClient = MongoClient('192.168.0.71', 27017)
fromClient=MongoClient('mongodb://192.168.0.71:27017/')
toClient=MongoClient('mongodb://127.0.0.1:27017/')

#子数据库
fromDb=fromClient.bydr_hall
toDB=toClient.bydr_hall

#集合
fromCollection=fromDb.role
toCollection=toDB.role

#删除之前的目标服已有相同集合
toCollection.remove();
print('删除'+toCollection.name)



#一行一行复制 TODO 批量复制？
for data in fromCollection.find():
    toCollection.insert_one(data)
    print('复制：%s' %(data))

print('源数据：%d ' % (fromCollection.count()))
print('目数据：%d ' % (toCollection.count()))

