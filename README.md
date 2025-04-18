
### 동시성은 무엇인가?
- 사전 지식
    - 프로세스 : 운영체제에서 실행 중인 하나의 애플리케이션
        - os에서 애플리케이션 실행 -> os에서 메모리 할당 -> 애플리케이션 코드를 실행
    - 스레드 : 사전적 정의 -> 한가닥의 실
        - 프로세스 내부에서 실행되는 독립적인 작업 단위
        - 프로세스의 자원을 공유
        - 한 프로세스 내부의 여러 개의 쓰레드도 있을 수 있다. 이를 멀티쓰레드 라고 한다.
    - 스레드 안전성 (Thread-safe)
        - 멀티 스레드 환경에서도 동시성 이슈가 발생하지 않고 프로그램이 동작하는 것
- 동시성
    - 멀티 작업을 위해 하나의 코어에서 멀티 스레드가 번갈아가며 실행하는 성질
    - 일종의 싱글 코어에서 멀티 스레드를 동작시키기 위한 방법
    - 작업이 동시에 진행 되는 것 처럼 보일뿐 실제로 동시에 진행되지는 않는다
- 동시성 이슈가 발생하는 이유
    - 한 프로세스 내에서 멀티쓰레드로 작업을 할 경우 발생
    - 프로세스에 할당된 자원을 멀티쓰레드가 공유하기 때문에 일관성을 유지하기 힘들어짐
### 자바에서 동시성 제어 하는 방법
- Lock
    - 암시적 Lock
        - 메서드나, 메서드 내의 블럭 안에 synchronized 키워드를 사용
        - 자동으로 lock, unlock 진행
        - **임계영역**을 설정하여 한 번에 하나의 스레드만 접근할 수 있도록 제한
            - 임계영역(critical section) : 공유데이터가 사용되어 동기화가 필요한 부분
        - 메서드 전체 동기화
            - 메서드에 진입 가능한 스레드가 단 하나!
        - 메서드 내 변수 동기화 , 이때 변수는 객체여야 한다.
            - 이 때 해당 변수를 단 하나의 스레드만 참조 가능
        - 장점
            - 선언함으로써 바로 사용가능
        - 단점
            - 한 스레드가 끝날때까지 다른 스레드는 접근을 못함 -> 자원낭비문제(속도도 느려짐)
    - 명시적 Lock
        - ReentrantLock
          ```
           private final ReentrantLock lock = new ReentrantLock(); // 락 선언
           public 어쩌고 method(){
               lock.lock();   // lock 실행
               ...코드 부분
               lock.unlock(); // lock 종료
           }
          ```
        - 직접 락을 획득/해제
        - 더 세밀한 동기화 필요시 사용
        - 장점
            - 세밀한 제어가능 -> 특정 코드 블록에 대한 락을 걸 수 있음
            - 공정성 설정
                - Lock 인스턴스 생성 시 -> true, false 을 입력하여 공정성 설정 가능
                - new ReentrantLock(true); : 공정한 락으로 가장 오래 기다린 스레드가 우선적으로 락 획득
                - new ReentrantLock(false); : 비공정한 락으로 락 획득 순서가 무작위(성능상 이점)
            - Interruptible Locking
                - 스레드가 락을 기다리는 동안 인터럽트를 받을 수 있음
                    - /////----/////--------////////
                    - ---//////---////////////-------
                - 이는 스레드가 대기 중에 다른 작업을 수행할 수 있도록 해줌
                - lock.lockInterruptibly() <<-- 이게 선언되어 있는 곳에서 인터럽트 가능
            - Condition Variable
                - Condition 객체를 사용하여 스레드 간의 통신을 쉽게 할 수 있음
                - 생산자-소비자 문제와 같은 복잡한 동기화 문제를 해결하는데 유용
        - 단점
            - 복잡성 : 락을 획득하고 해제하는데 로직을 명시해야하기 때문
            - 성능 오버헤드 : 락을 사용하면 성능 오버헤드가 발생 할 수 있음 -> 락을 자주 획득하고 헤제하는 경우 성능 저하 발생
            - 데드락 위험
                - 여러 락을 동시에 사용하는 경우 데드락이 발생
                - = 2개 이상 스레드가 서로의 락을 기다리면 무한 대기 상태에 빠지는 상황
- volatile 키워드
    - java 변수를 캐싱하는 것이 아닌 main memory에 올림
    - I/O 모두 메인 메모리 사용하기에 쓰레드 간 공유 변수를 안전하게 읽고 쓰기가 가능
    - 변수의 값을 모든 스레드에서 항상 최신 값으로 유지
    - 장점
        - 값을 즉시 반영 가능
        - 단순 플래그 작업시 유리
    - 단점
        - 원자성 보장이 안됨
- Atomic (cas 기반)
    - Lock/synchronized 사용하지 않고 원자적 연산을 제공(논블로킹 방식)
    - 내부적으로 Compare - And - Swap 을 사용하여 동기화 비용을 줄임
    - 장점
        - 락을 사용하지 않으므로 높은 성능
        - 데드락이 없음
    - 단점
        - 복잡한 연산에는 부적합
        - 경쟁 상태가 많을 경우 cas 실패로 인해 성능 저하 가능
- 동시성 컬렉션 (Concurrent Collections)
    - 자바에서 제공하는 동시성 컬렉션을 사용
    - java.util.concurrent 패키지 사용
    - 자바 컬렉션을 동기화 제어를 하기 위한 용도로 만들어서 자바 컬렉션과 비슷한 사용법
        - ConcurrentHashMap
        - CopyOnWriteArraySet / CopyOnWriteArrayList
            - CopyOnWrite 방식은 데이터변경 작업 시 내부적으로 복사본을 하나 더 만들어 작업
        - ConcurrentLinkedQueue
        - 등등
    - 장점
        - synchronized 달리 부분락이나 cas 방식으로 성능저하 방지
        - 기존 컬렉션과 유사한 사용방식
        - 데드락 위험 없음
    - 단점
        - 전체 데이터를 독점적으로 사용해야할 경우 성능 문제 발생 할 수 있음
- 멀티쓰레드 구현 방법
```
	- 쓰레드를 배열에 담아서 최종적으로 모든 쓰레드가 대기될 때까지 기다림 
	    -> arrayList 보단 동시성 컬렉션에 있는 리스트 컬렉션을 사용하는게 좋을 것 같다
	List<Thread> threads = new ArrayList<>();
	for (int i = 0; i < threadCount; i++){  
	    Thread thread = new Thread(()->{  
	        작업할 코드 구현 위치
		    });  
	    threads.add(thread);  
	    thread.start();  // 각 쓰레드 실행
	}  
	for(Thread thread: threads){  
	    thread.join();  // 각 쓰레드 완료시 까지 대기
	}

	- CountDownLatch 사용
	 일련의 스레드 작업이 끝난 후 다음 작업이 진행될 수 있도록 대기 기능을 제공
	 생성자를 통해서 생성
	 주요 메서드
	 await(); -> 생성자에 주입한 count의 개수가 0이 될때까지 현재 스레드 대기
	 await(long timeout, TimeUnit unit); -> 위와 같되, 최대 정해진 시간까지만 대기하고 해당 시간까지 count가 0이 되지 않으면 대기를 해제
	 countDown(); -> 생성자에 주입한 개수를 감소시켜줌, 0에 도달하면 대기중이던 모든 스레드를 해제
	 getCount(); -> 현재 latch 개수를 반환
```
- 참고자료
    - [멀티스레드의 동시성 이슈](https://velog.io/@mooh2jj/%EB%A9%80%ED%8B%B0-%EC%8A%A4%EB%A0%88%EB%93%9C%EC%9D%98-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88)
    - [동시성제어](https://velog.io/@bbbbooo/Java-%EC%A2%8B%EC%95%84%EC%9A%94%EB%A5%BC-%ED%86%B5%ED%95%B4-%EB%B3%B4%EB%8A%94-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4)
    - 책 - 이것이 자바다 : 멀티 스레드 챕터 참고