from bs4 import BeautifulSoup
from urlparse import urljoin
import urllib2
import argparse
import re
import codecs
import time
import random

get_yelp_page = \
    lambda zipcode, page_num: \
    	  'http://www.yelp.com/search?find_loc={0}&start={1}&cflt=restaurants'.format(zipcode, page_num)

ZIP_URL = "zipcodes.txt"
FIELD_DELIM = u'###'
LISTING_DELIM = u'((('

def get_zips():
    """
    """
    f = open(ZIP_URL, 'r+')
    zips = [int(zz.strip()) for zz in f.read().split('\n') if zz.strip() ]
    f.close()
    return zips

def crawl_page(zipcode, page_num, verbose=False):
    """
    This method takes a page number, yelp GET param, and crawls exactly
    one page. We expect 10 listing per page.
    """
    try:
        page_url = get_yelp_page(zipcode, page_num)
        soup = BeautifulSoup(urllib2.urlopen(page_url).read())
    except Exception, e:
        print str(e)
        return []

    restaurants = soup.findAll('div', {'class': 'search-result natural-search-result'})
    try:
        assert(len(restaurants) == 10)
    except AssertionError, e:
        print 'we have hit the end of the zip code', str(e)
        return [], False

    extracted = [] # a list of tuples
    for r in restaurants:
        img = ''
        yelpPage = ''
        title = ''
        rating = ''
        addr = ''
        phone = ''
        categories = ''
        menu = ''
        creditCards = ''
        parking = ''
        attire = ''
        groups = ''
        kids = ''
        reservations = ''
        delivery = ''
        takeout = ''
        waiterService = ''
        outdoor = ''
        wifi = ''
        price = ''
        goodFor = ''
        alcohol = ''
        noise = ''
        ambience = ''
        tv = ''
        caters = ''
        wheelchairAccessible = ''
        try:
            img = r.div('div', {'class':'media-avatar'})[0].img['src']
        except Exception, e:
            if verbose: print 'img extract fail', str(e)
        try:
            title = r.find('a', {'class':'biz-name'}).getText()
        except Exception, e:
            if verbose: print 'title extract fail', str(e)
        try:
            yelpPage = r.find('a', {'class':'biz-name'})['href']
        except Exception, e:
            if verbose: print 'yelp page link extraction fail', str(e)
            continue
        try:
            categories = r.findAll('span', {'class':'category-str-list'})
            categories = ', '.join([c.getText() for c in categories if c.getText()])
        except Exception, e:
            if verbose: print "category extract fail", str(e)
        try:
            rating = r.find('i', {'class':re.compile(r'^star-img')}).img['alt']
        except Exception, e:
            if verbose: print 'rating extract fail', str(e)
        try:
            addr = r.find('div', {'class':'secondary-attributes'}).address.getText()
        except Exception, e:
            if verbose: print 'address extract fail', str(e)
        try:
            phone = r.find('div', {'class':'secondary-attributes'}).find('span', {'class': 'biz-phone'}).getText()
        except Exception, e:
            if verbose: print 'phone extract fail', str(e)


        if title: print 'title:', title
        if categories: print 'categories:', categories
        if rating: print 'rating:', rating
        if img: print 'img:', img
        if addr: print 'address:', addr
        if phone: print 'phone:', phone

        print '=============='
        # extracted.append((title, categories, rating, img, addr, phone, price, menu,
        #    creditCards, parking, attire, groups, kids, reservations, delivery, takeout,
        #    waiterService, outdoor, wifi, goodFor, alcohol, noise, ambience, tv, caters,
        #    wheelchairAccessible))

    return extracted, True

def crawl(zipcode=None):
    page = 0
    flag = True
    some_zipcodes = [zipcode] if zipcode else get_zips()

    if zipcode is None:
        print '\n**We are attempting to extract all zipcodes in America!**'

    for zipcode in some_zipcodes:
        print '\n===== Attempting extraction for zipcode <', zipcode, '>=====\n'
        while flag:
            extracted, flag = crawl_page(zipcode, page)
            if not flag:
                print 'extraction stopped or broke at zipcode'
                break
            page += 10
            time.sleep(random.randint(1, 2) * .931467298)
    print 'total page is',page

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Extracts all yelp restaurant \
        data from a specified zip code (or all American zip codes if nothing \
        is provided)')
    parser.add_argument('-z', '--zipcode', type=int, help='Enter a zip code \
        you\'t like to extract from.')
    args = parser.parse_args()
    crawl(args.zipcode)
