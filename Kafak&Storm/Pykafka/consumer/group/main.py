import logging, time
import group_consumer


def main():

    conusmer_thread = group_consumer.Consumer()
    conusmer_thread.start()

    time.sleep(500000)

if __name__ == '__main__':
    #logging.basicConfig(
    #    format='%(asctime)s.%(msecs)s:%(name)s:%(thread)d:%(levelname)s:%(process)d:%(message)s',
    #    level=logging.INFO
    #    )
    main()
