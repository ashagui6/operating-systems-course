#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/mman.h>
#include <math.h>

#define NUM_CHILDREN 4

// global variables
int *avg;
int *min;
int *max;
int *medium;

int compare(const void *a, const void *b) {
    int int_a = *(const int *)a;
    int int_b = *(const int *)b;

    // returns negative if a < b, zero if a == b, positive if a > b
    return (int_a > int_b) - (int_a < int_b);
}

int main(int argc, char *argv[]) {
    //argc = total number of items on the command line
    //argv = array of strings containing the items
    // check if user provided arguments 
    if (argc < 2) {
        printf("Error: missing arguments.\n");
        return 1; // exit with an error code 
    }

    // convert the command line strings into integers using atoi()
    // and store them in an integer array
    int num_elements = argc - 1;
    int *int_array = malloc(num_elements * sizeof(int));
    if (int_array == NULL) {
        fprintf(stderr, "Memory allocation failed\n");
        return 1;
    }

    for (int i = 0; i < num_elements; i++) {
        int_array[i] = atoi(argv[i+1]);
    }

    // sort the array
    qsort(int_array, num_elements, sizeof(int), compare);

    //allocate shared memory between parent and child processes
    avg = mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    min = mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    max = mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    medium = mmap(NULL, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    
    if (avg == MAP_FAILED || min == MAP_FAILED ||
    max == MAP_FAILED || medium == MAP_FAILED) {
    perror("mmap failed");
    exit(1);
}
    
    for (int i = 0; i < NUM_CHILDREN; i++) {
        pid_t pid = fork();

        if (pid < 0) {
            // error handling 
            perror("Fork failed");
            exit(1);
        }
        if (pid == 0) {
            if (i == 0) {
                //printf("--> 1st Child created: average\n");
                int sum = 0;
                for (int i = 0; i < num_elements; i++) {
                    sum += int_array[i];
                }
                *avg = (int)floor((double)sum/num_elements);
            }
            else if (i == 1) {
                //printf("--> 2nd Child created: min\n");
                *min = int_array[0];
            }
            else if (i == 2) {
                //printf("--> 3rd Child created: max\n");
                *max = int_array[num_elements - 1];
            }
            else if (i == 3) {
                //printf("--> 4th Child created: medium\n");
                //if odd num of elements, take the middle number
                if (num_elements % 2 != 0)
                {
                    *medium = int_array[ (int)floor((double)num_elements/2) ];
                }
                //if even num of elements, take the two middle numbers, add them and divide by 2
                else if (num_elements % 2 == 0) {
                    *medium = (int_array[ (num_elements/2) - 1] + int_array[ (num_elements/2)]) / 2;
                }
            }
            exit(0);
        }
    }
    for (int i = 0; i < NUM_CHILDREN; i++) wait(NULL);
    printf("The average value is %d\n", *avg);
    printf("The minimum value is %d\n", *min);
    printf("The maximum value is %d\n", *max);
    printf("The medium value is %d\n", *medium);

    munmap(avg, sizeof(int));
    munmap(min, sizeof(int));
    munmap(max, sizeof(int));
    munmap(medium, sizeof(int));
    return 0;
}