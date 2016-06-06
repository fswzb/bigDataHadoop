# -*- coding: utf-8 -*-
"""
This module provide kafka sync and async producer.
"""

import logging, time

from async.ASyncProducer import ASyncProducer
from sync.SyncProducer import SyncProducer


def main():
    threads = [
        ASyncProducer(),
        SyncProducer()
    ]

    for t in threads:
        t.start()

    time.sleep(5000)

if __name__ == "__main__":
    #logging.basicConfig(
    #    format='%(asctime)s.%(msecs)s:%(name)s:%(thread)d:%(levelname)s:%(process)d:%(message)s',
    #    level=logging.INFO
    #    )
    main()