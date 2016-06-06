import threading
from kafka.client import KafkaClient
from kafka.consumer import SimpleConsumer

class Consumer(threading.Thread):
    daemon = True
    def __init__(self,partition_index):
        threading.Thread.__init__(self)
        self.part = [partition_index]
        self.__offset = 0


    def run(self):
        client = KafkaClient("10.206.216.13:19092,10.206.212.14:19092,10.206.209.25:19092")
        consumer = SimpleConsumer(client, "test-group", "guantest",auto_commit=False,partitions=self.part)

        consumer.seek(0,0)

        while True:
            message = consumer.get_message(True,60)
            self.__offset = message.offset
            print message.message.value
