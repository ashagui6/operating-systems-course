/**
 * Producer Consumer
 * using counting semaphores as well as a mutex lock.
 */

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>
#include <time.h>

#define TRUE 1
#define BUFFER_SIZE 5
#define MAX_SLEEP 5

#define MIN 0  //minimum value of an element in buffer
#define MAX 1000 //maximum value of an element in buffer

int buffer[BUFFER_SIZE];

pthread_mutex_t mutex;
sem_t empty;
sem_t full;
time_t start_time;

int insertPointer = 0, removePointer = 0;

void *producer(void *param);
void *consumer(void *param);

int elapsed_time() {
	return (int)(time(NULL) - start_time);
}

int myRand() {
   return rand() % (MAX - MIN + 1) + MIN;
}

int print_buffer(){
	int i;
	for (i=0; i< BUFFER_SIZE; i++)
		printf("slot %d: %d\n", i, buffer[i]);
	printf("\n");	
	return 0;
}

int init_buffer(){
	int i;
	for (i=0; i< BUFFER_SIZE; i++)
		buffer[i] = -1;
	return 0;
}

int insert_item(int item, int id)
{
	// insert code here
	// Producer tries to insert random at current_time
	sem_wait(&empty);
	pthread_mutex_lock(&mutex);

	// add insert_item to the buffer
	buffer[insertPointer] = item;
	insertPointer = (insertPointer + 1) % BUFFER_SIZE;
	printf("Producer %d produced %d at time %d\n", id, item, elapsed_time());
	print_buffer();

	pthread_mutex_unlock(&mutex);
	sem_post(&full);

	return 0;
}

int remove_item(int id)
{
	int item;
	// insert consumer code here
	sem_wait(&full);
	pthread_mutex_lock(&mutex);

	// remove an item from buffer
	item = buffer[removePointer];
	buffer[removePointer] = -1;
	removePointer = (removePointer + 1) % BUFFER_SIZE;
	printf("Consumer %d consumed %d at time %d\n", id, item, elapsed_time());
	print_buffer();

	pthread_mutex_unlock(&mutex);
	sem_post(&empty);

	// Consumer tries to consume random at current_time

	return 0;
}

int main(int argc, char *argv[])
{
	int sleepTime, producerThreads, consumerThreads;
	int i, j;

	if(argc != 4)
	{
		fprintf(stderr, "Useage: <sleep time> <producer threads> <consumer threads>\n");
		return -1;
	}

	sleepTime = atoi(argv[1]);
	producerThreads = atoi(argv[2]);
	consumerThreads = atoi(argv[3]);

	/* Initialize buffer to be -1 */
	init_buffer();

	/* Initialize the synchronization tools */
	printf("%d\n",pthread_mutex_init(&mutex, NULL));
	printf("%d\n",sem_init(&empty, 0, BUFFER_SIZE));
	printf("%d\n",sem_init(&full, 0, 0));
	srand(time(0));
	start_time = time(NULL);

	/* Create the producer and consumer threads */
	for(i = 0; i < producerThreads; i++)
	{
		pthread_t tid;
		pthread_attr_t attr;
		int *id = malloc(sizeof(int));
		*id = i + 1;
		pthread_attr_init(&attr);
		pthread_create(&tid, &attr, producer, id);
	}

	for(j = 0; j < consumerThreads; j++)
	{
		pthread_t tid;
		pthread_attr_t attr;
		int *id = malloc(sizeof(int));
		*id = j + 1;
		pthread_attr_init(&attr);
		pthread_create(&tid, &attr, consumer, id);
	}

	/* Sleep for user specified time */
	sleep(sleepTime);
	return 0;
}


void *producer(void *param)
{
	int id = *(int *)param;
	free(param);
	int random;
	int r;

	while(TRUE)
	{
		r = rand() % MAX_SLEEP;
		sleep(r);
		random = myRand();

		printf("Producer %d tries to insert %d at time %d\n", id, random, elapsed_time()); 
		if(insert_item(random, id))
			fprintf(stderr, "Error");

	}

}

void *consumer(void *param)
{
	int id = *(int *)param;
	free(param);
	int r;

	while(TRUE)
	{
		r = rand() % MAX_SLEEP;
		sleep(r);

		printf("Consumer %d tries to consume at time %d\n", id, elapsed_time()); 

		if(remove_item(id))
				fprintf(stderr, "Error Consuming");
	}
}
