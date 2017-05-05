'''
Created on Aug 29, 2016
 
@author: loucelj
'''
import unittest
from CoPrimeGenerator import CoPrimeGenerator
 
class CoPrimeTest(unittest.TestCase):

        
    @classmethod
    def setUpClass(cls):
        cls.generator = CoPrimeGenerator(15,15)
        
        
    def testCanCreateInitializedMatrix(self):
        #if initialized properly the value at 1,1 should be a '*'
        expected = '*'
        actual = self.generator.getMatrixValForPair(1,1)
        self.assertEquals(expected, actual)
  
  
    def testCoPrimePairGeneratedCorrectly(self):
        #co-prime pair (4,9) should have a '*' in result matrix
        expected = '*'
        actual = self.generator.getMatrixValForPair(4,9)
        self.assertEquals(expected, actual)
 
          
    def testNonCoPrimePairGeneratedCorrectly(self):
        #co-prime pair (4,10) should have a ' ' in result matrix
        expected = ' '
        actual = self.generator.getMatrixValForPair(4, 10)
        self.assertEquals(expected, actual)
        

    def testIndexOutOfBoundsForGetMatrixValForPairRow(self):
        #if invalid values are passed in returns -1
        expected = -1
        actual = self.generator.getMatrixValForPair(20,2)
        self.assertEquals(expected,actual)
        
                  
    def testIndexOutOfBoundsForGetMatrixValForPairCol(self):
        #if invalid values are passed in returns -1
        expected = -1
        actual = self.generator.getMatrixValForPair(2,20)
        self.assertEquals(expected,actual)
        
        
    def testIndexOutOfBoundsForGetMatrixValForPairRowCol(self):
        #if invalid values are passed in returns -1
        expected = -1
        actual = self.generator.getMatrixValForPair(20,20)
        self.assertEquals(expected,actual) 
        
           
    def testIsCoPrimeTrue(self):
        #return true if a valid co-prime pair is passed
        self.assertTrue(self.generator.isCoprimePair(4,9))
        
        
    def testIsCoPrimeFalse(self):
        #return true if a valid co-prime pair is passed
        self.assertFalse(self.generator.isCoprimePair(4,10)) 
        
    
    def testCoPrimePointInPrintCoPrimePairsList(self):
        #validate expected pair is in coprime pair list
        self.assertIn((4,9), self.generator.getCoPrimePairList())
        
        
    def testCoPrimePointNotInPrintCoPrimePairsList(self):
        #validate non coprime pair not in list
        self.assertNotIn((4,10), self.generator.getCoPrimePairList())
        
           
if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()