'''
    CoPrime.py

    Generates a graph of the m x n co-primes
    
    Modified on Aug 29, 2016

    @author: Jason Loucel
'''

import sys
from fractions import gcd


class CoPrimeGenerator:
    
   
    coPrimeMatrix = [None]
    coPrimePairList = []
    rows = 0
    cols = 0
    
    '''
    When a CoPrime object is created we create a matrix and generate
    the co-prime pairs.
    '''
    def __init__(self,a,b):
        self.rows = a
        self.cols = b
        self.coPrimeMatrix = [None] * self.rows
        self.initMatrix()
        self.generateCoprimeMatrix()
        self.generateCoPrimePairList()
        
            
    '''
    creates a list of size n each with
    each element initialized to a space ' '
    '''
    def initMatrix(self):
        
        for i in range(self.rows):
            self.coPrimeMatrix[i] = [' '] * (self.cols) 
      

    '''
    co-prime's can be evaluated using the built-in 
    gcd function. If the result of the eval is 1 
    i.e. the only factor in common is 1, the pair
    is said to be co-prime. This function improves
    readability.
    '''
    def isCoprimePair(self,x,y):
        if gcd(x,y) == 1:
            return True
        return False
    
    '''
    sets the value of the index in matrix to '*' where
    the index values are co-prime. We add 1 to each index
    evaluation to account for zero based index counters.
    '''
    def generateCoprimeMatrix(self):
    
        for i in range(self.rows):
            for j in range(self.cols):
                if self.isCoprimePair(i+1,j+1):
                    self.coPrimeMatrix[i][j] = '*' 
    
    
    '''
    output the co-prime matrix
    '''
    def printCoprimeMatrix(self):
        print '\rCo-Prime Matrix in Range ' + str(self.rows) + ' x ' \
                                            + str(self.cols) + '\r\r'
        for x in reversed(self.coPrimeMatrix):
            # x[:] is a list "slice"
            for y in x[:]:
                '''
                by putting a comma at the end, we prevent a newline
                '''
                print y,
                
            print
    
    
    '''
    generates a list of co-prime points based on input range
    '''
    def generateCoPrimePairList(self):
        for i in range(self.rows):
            for j in range(self.cols):
                if self.coPrimeMatrix[i][j] == '*':
                    point = (i+1,j+1);
                    self.coPrimePairList.append(point)
    
    '''
    output the co-prime pair list
    '''
    def printCoPrimePairs(self):
        count = 0
        print '\rCo-Prime Points in Range ' + str(self.rows) + ' x ' \
                                            + str(self.cols) + '\r\r'
        for point in self.coPrimePairList:
            count+=1
            if count % 5 == 0:
                print
            print '{:8}'.format(point),
    
    
    '''
    returns the coPrime pair list
    '''       
    def getCoPrimePairList(self):
        return self.coPrimePairList
    
    
    '''
    returns the coPrime pair matrix
    '''       
    def getCoPrimePairMatrix(self):
        return self.coPrimeMatrix
    
    
    ''' 
    returns -1 if values are outside of matrix range
    or return the value stored for the passed index
    '''
    def getMatrixValForPair(self,a,b):
        if a > self.rows or b > self.cols:
            return -1
        else: 
            val = self.coPrimeMatrix[a-1][b-1]
        return val
    
    
    if __name__ == "__main__":
        # some error checking
        if len(sys.argv) != 3:
            print 'Usage\n python CoPrime [int] [int]'
            quit()
