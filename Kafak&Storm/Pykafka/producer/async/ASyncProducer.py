# -*- coding: utf-8 -*-
"""
This module provide kafka partition and group consumer.
"""

import threading, time

from kafka.client import KafkaClient
from kafka.producer import SimpleProducer

class ASyncProducer(threading.Thread):
    daemon = True

    def run(self):
        client = KafkaClient("10.206.216.13:19092,10.206.212.14:19092,10.206.209.25:19092")
        producer = SimpleProducer(client,async=True)

        while True:
            producer.send_messages('guantest', "test")
            producer.send_messages('guantest', "test")

            time.sleep(1)