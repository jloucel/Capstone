/**
 * Demonstration C program illustrating how to perform file I/O for vm assignment.
 *
 * Input file contains logical addresses
 * 
 * Backing Store represents the file being read from disk (the backing store.)
 *
 * We need to perform random input from the backing store using fseek() and fread()
 *
 * This program performs nothing of meaning, rather it is intended to illustrate the basics
 * of I/O for the vm assignment. Using this I/O, you will need to make the necessary adjustments
 * that implement the virtual memory manager.
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

// number of characters to read for each line from input file
#define BUFFER_SIZE         10

// number of bytes to read
#define CHUNK               256

// mem ofsets
#define PAGE_MASK 0x000000FF;
#define OFFSET_MASK 0x000000FF;

// datatype to hold page and frame info for tlb
typedef struct tlbData{
	int page;
	int frame;
} tlbData;

FILE    *address_file;
FILE    *backing_store;

// how we store reads from input file
char    address[BUFFER_SIZE];

int     logical_address;

// the buffer containing reads from backing store
signed char     buffer[CHUNK];

// the value of the byte (signed char) in memory
signed char     value;

int main(int argc, char *argv[])
{
signed char memBlock[CHUNK][CHUNK];
int memMap[CHUNK];
tlbData tlbTable[16];
int page, offset, usedBlock, i, j, physicalAddress;
int pageFault, addressCount, tlbHit, tlbCounter;
float faultRate, tlbHitRate;

    // init arrays

    for(i = 0; i < CHUNK; i++){
	memMap[i] = -1;
    }

    for(i = 0; i < 16; i++){
	    tlbTable[i].page = -1;
	    tlbTable[i].frame = -1;
    }

    // perform basic error checking
    if (argc != 3) {
        fprintf(stderr,"Usage: ./vm [backing store] [input file]\n");
        return -1;
    }

    // open the file containing the backing store
    backing_store = fopen(argv[1], "rb");
    
    if (backing_store == NULL) { 
        fprintf(stderr, "Error opening %s\n",argv[1]);
        return -1;
    }

    // open the file containing the logical addresses
    address_file = fopen(argv[2], "r");

    if (address_file == NULL) {
        fprintf(stderr, "Error opening %s\n",argv[2]);
        return -1;
    }

    addressCount = 0;
    usedBlock = 0;
    pageFault = 0;
    tlbCounter = 0;
    tlbHit = 0;

    // read through the input file and output each logical address
    while ( fgets(address, BUFFER_SIZE, address_file) != NULL) {
        logical_address = atoi(address);
	addressCount++;
	page = (logical_address >> 8) & PAGE_MASK;
	offset = logical_address & OFFSET_MASK;	
	
	// check to see if we have a reference to the frame in tlb
	for(i = 0; i < 16; i++){
		if(tlbTable[i].page == page){ 
			tlbTable[i].frame = usedBlock; 
			tlbHit++;
		}
	}
	
	// check to see if the page has a reference to memory
	if(memMap[page] == -1) {
		
		// if we enter this section of code we didn't find a mem ref, this is a fault
		pageFault++;

		// first seek to byte CHUNK in the backing store
		// SEEK_SET in fseek() seeks from the beginning of the file
		if (fseek(backing_store, CHUNK * page, SEEK_SET) != 0) {
		    fprintf(stderr, "Error seeking in backing store\n");
		    return -1;
		}

		// now read CHUNK bytes from the backing store to the buffer
		if (fread(&memBlock[usedBlock], sizeof(signed char), CHUNK, backing_store) == 0) {
		    fprintf(stderr, "Error reading from backing store\n");
		    return -1;
		}
	
		// update the pointers so that we can reference the newly stored page
		memMap[page] = usedBlock;
		usedBlock++;
		tlbCounter = (tlbCounter + 1) % 16;
		tlbTable[tlbCounter].page = page;
		tlbTable[tlbCounter].frame = usedBlock;
		
	}
	
	// revers the translation 
	physicalAddress = (memMap[page] << 8) + offset;

	printf("Virtual address: %i ", logical_address); 
	printf("Physical address: %i ", physicalAddress);
	printf("Value: %d\n",memBlock[memMap[page]][offset]);

    }

    // print out statistical info for this run
    faultRate = (double) pageFault/addressCount;
    tlbHitRate = (double) tlbHit/addressCount;
    printf("PageFaults: %i\n", pageFault); 
    printf("Addresses Translated: %i\n", addressCount);
    printf("Page Fault Rate: %.3f\n", faultRate);
    printf("TLB Hits: %i\n", tlbHit);
    printf("TLB Fault Rate: %.3f\n", tlbHitRate);
    fclose(address_file);
    fclose(backing_store);

    return 0;
}


