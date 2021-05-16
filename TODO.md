api_glue_job_run 테이블 상태 코드
start, running, complete, stop, fail


1. job 등록 (createJob)
   - api_glue_job 테이블에 등록
   - glue_sch_qrtz_job_details 테이블에 등록한다.
   - job 등록 시 glue는 job group 개념이 없기 때문에 userName을 group 컬럼에 등록하게 함.
     jobRepository.save()
     scheduler.addJob(jobKey("JOB1", "ADMIN"));
   
2. job 실행 (startJobRun)
   - 스케줄 없이 한번만 실행한다.
   - api_glue_job 테이블에 이미 존재해야함.
   - glue_sch_qrtz_job_details 테이블에 이미 존재해야함.
   - api_glue_job_run 에 등록됨
   - 한번만 실행되고 스케줄링은 걸리지 않음.
   - api_glue_job_run 테이블의 triggerId는 null 상태 임.
   - api_glue_job_run 테이블의 job_run_state 필드가 "COMPLETE" 로 변경됨.
   
3. create trigger 
   - api_glue_job 테이블에 이미 존재해야함.
   - glue_sch_qrtz_job_details 테이블에 이미 존재해야함.
   - api_glue_trigger 테이블에 저장함.
   - startOnCreate : false
      - api_glue_trigger 테이블에 저장하고 종료 함.
      - quartz 테이블에 등록하지 않는 이유는 등록과 동시에 스케줄링이 됨.
     startOnCreate : true
      - glue_sch_qrtz_triggers 테이블에 저장됨과 동시에 스케줄러 start
      - api_glue_job_run 에 데이터를 생성한다.
        - triggerId가 입력됨
        - job_run_state "RUNNING"으로 update 됨
    참고 : 
      glue는 하나의 스케줄에 여러개의 jobName을 실행 할 수 있는데
      quartz는 하나에만 입력할 수 있게 되어 있어서 일단 하나만 입력하고
      StartJob 클래스에서 실행시에는 triggerName으로 glue 테이블에 Action 컴럼을 조회해서 실행한다.
   
4. start trigger 
   - api_glue_job 테이블에 이미 존재해야 함.
   - glue_sch_qrtz_job_details 테이블에 이미 존재해야 함.
   - api_glue_trigger 테이블에 존재 해야 함.
   - create trigger 에서 startOnCreate이 false로 생성한 경우다.
   - 이미 3번에서 job과 trigger를 저장하였으므로 
     스케줄만 on 시켜준다. 
     api-server에서 수행하지 않고 schedule-server로 addTrigger 메소드만 호출해준다.
   - api_glue_job_run 에 데이터를 생성한다.(triggerId와 state(RUNNING))
   
5. stop trigger 
   - api_glue_job 테이블에 이미 존재해야 함.
   - glue_sch_qrtz_job_details 테이블에 이미 존재해야 함.
   - api_glue_trigger 테이블에 존재 해야 함.
   - api_glue_job_run테이블과 api_glue_trigger의 state컬럼을 STOP으로 변경

5. delete trigger 
 - api_glue_job 테이블에 이미 존재해야 함.
 - glue_sch_qrtz_job_details 테이블에 이미 존재해야 함.
 - api_glue_trigger 테이블에 존재 해야 함.
 - quartz 스케줄이 걸려 있는경우
   api_glue_job_run 테이블과 state컬럼을 DELETED로 UPDATE
   api_glue_trigger의 state컬럼을 "DELETED"로 UPDATE 할지 삭제 할지 결정해야 함. (현재는 삭제로 구현함.)
   quartz 트리거와 스케줄 삭제
 - quartz 스케줄이 없는경우
   api_glue_trigger 테이블에서 삭제
 
6. update trigger
 - 스케줄이 걸려 있지 않은 경우는
   api_glue_job, api_glue_trigger만 수정하면 됨.
 - 스케줄이 걸려 있는 경우는?
   해당 스케줄을 삭제하고 재등록 해야 하는지 아니면 스케줄이 걸려 있는 경우는 수정이 안되게 하고 
   사용자가 삭제 후 재등록 하게 해야 할지? 
 

7. Delete job
 - 스케줄이 걸려 있지 않은 경우
   api_glue_job 삭제
   glue_sch_qrtz_job_details 삭제
 - 스케줄이 걸려 있는 경우
   api_glue_job 삭제
   glue_sch_qrtz_job_details 삭제
   quartz의 경우 job과 trigger가 1-1 매핑이 되지만
   qlue의 경우 하나의 trigger에 여러개의 job이 포함된 경우가 있는데
   해당 job이 포함된 트리거를 모두 삭제해야 하는지?
   트리거 생성시 actions 컬럼이 리스트 형식으로 되어 있어 여러개의 job을 수행할 수 있음

 
8. UpdateJob
   api_glue_job 테이블 수정
   quartz 테이블 수정은 하지 않는다 수정할 컬럼이 없다.
   수정은 jobId를 조회 후 jobId를 키로 수정을 해야함.
   jobName 컬럼은 수정 시 기존에 걸려있는 Trigger에 영향이 크므로
   jobName은 수정은 불가하다는 규칙을 가지고 있어야 할 것 같음.
   jobName의 수정이 필요한 경우 삭제 후 재등록 해야 할 것 같음.
 
9. killJob
 - glue Api에는 없는 기능임
 - 의미가 실행중인 process를 kill 한다는 의미 인지 실행중인 스케줄을 kill한다는 것인지 확인 필요
 - 스케줄이 걸려 있는 경우를 대상으로 함.
   quartz의 경우 job과 trigger가 1-1 매핑이 되지만
   qlue의 경우 하나의 trigger에 여러개의 job이 포함된 경우가 있는데
   해당 job이 포함된 트리거를 모두 kill 해야 하는지?

10. BatchStopJobRun
 
 - 스케줄이 걸려 있는 경우
   quartz의 경우 job과 trigger가 1-1 매핑이 되지만
   qlue의 경우 하나의 trigger에 여러개의 job이 포함된 경우가 있는데
   해당 job이 포함된 트리거를 모두 중지 해야 하는지?

2021년 5월 16일 (일) 오후 6:55, 강정화 <junghwa.k@data-dynamics.io>님이 작성:
안녕하세요 단순 조회를 제외한 job, trigger 실행에대한
glue 현재 구현한 기능에 대한 설명입니다.보시고 다른 의견이 있는지 답변 바랍니다.

1. job 등록 (createJob)
 - api_glue_job 테이블에 등록
 - glue_sch_qrtz_job_details 테이블에 등록한다.
 - job 등록 시 glue는 job group 개념이 없기 때문에 userName을 group 컬럼에 등록하게 함.
 jobRepository.save()
 scheduler.addJob(jobKey("JOB1", "ADMIN"));
 
2. job 실행 (startJobRun)
 - 스케줄 없이 한번만 실행한다.
 - api_glue_job 테이블에 이미 존재해야함.
 - glue_sch_qrtz_job_details 테이블에 이미 존재해야함.
 - api_glue_job_run 에 등록됨
 - 한번만 실행되고 스케줄링은 걸리지 않음.
 - api_glue_job_run 테이블의 triggerId는 null 상태 임.
 - api_glue_job_run 테이블의 job_run_state 필드가 "COMPLETE" 로 변경됨.
 
3. create trigger 
 - api_glue_job 테이블에 이미 존재해야함.
 - glue_sch_qrtz_job_details 테이블에 이미 존재해야함.
 - api_glue_trigger 테이블에 저장함.
 - startOnCreate : false
 - api_glue_trigger 테이블에 저장하고 종료 함.
 - quartz 테이블에 등록하지 않는 이유는 등록과 동시에 스케줄링이 됨.
 startOnCreate : true
 - glue_sch_qrtz_triggers 테이블에 저장됨과 동시에 스케줄러 start
 - api_glue_job_run 에 데이터를 생성한다.
 - triggerId가 입력됨
 - job_run_state "RUNNING"으로 update 됨
 참고 : 
 glue는 하나의 스케줄에 여러개의 jobName을 실행 할 수 있는데
 quartz는 하나에만 입력할 수 있게 되어 있어서 일단 하나만 입력하고
 StartJob 클래스에서 실행시에는 triggerName으로 glue 테이블에 Action 컴럼을 조회해서 실행한다.
 
4. start trigger 
 - api_glue_job 테이블에 이미 존재해야 함.
 - glue_sch_qrtz_job_details 테이블에 이미 존재해야 함.
 - api_glue_trigger 테이블에 존재 해야 함.
 - create trigger 에서 startOnCreate이 false로 생성한 경우다.
 - 이미 3번에서 job과 trigger를 저장하였으므로 
 스케줄만 on 시켜준다. 
 api-server에서 수행하지 않고 schedule-server로 addTrigger 메소드만 호출해준다.
 - api_glue_job_run 에 데이터를 생성한다.(triggerId와 state(RUNNING))
 
5. stop trigger 
 - api_glue_job 테이블에 이미 존재해야 함.
 - glue_sch_qrtz_job_details 테이블에 이미 존재해야 함.
 - api_glue_trigger 테이블에 존재 해야 함.
 - api_glue_job_run테이블과 api_glue_trigger의 state컬럼을 STOP으로 변경

5. delete trigger 
 - api_glue_job 테이블에 이미 존재해야 함.
 - glue_sch_qrtz_job_details 테이블에 이미 존재해야 함.
 - api_glue_trigger 테이블에 존재 해야 함.
 - quartz 스케줄이 걸려 있는경우
 - api_glue_job_run 테이블과 state컬럼을 DELETED로 UPDATE
 - api_glue_trigger의 state컬럼을 "DELETED"로 UPDATE 할지 삭제 할지 결정해야 함.
 - quartz 트리거와 스케줄 삭제
 - quartz 스케줄이 없는경우
 - api_glue_job_run 테이블과 state컬럼을 DELETED로 UPDATE
 - api_glue_trigger의 state컬럼을 "DELETED"로 UPDATE 할지 삭제 할지 결정해야 함.
 

   
   
3. job 삭제 (DeleteJob)
   api_glue_job 테이블 삭제
   quartz 테이블 삭제
      - 만약 스케줄이 돌고 있다면 
        trigger 삭제
        스케줄 삭제
        
4. job 시작
   job이 있는지 trigger가 있는지 확인 
   job과 trigger가 둘다 등록되어 있을때 스케줄링하기
   
5. trigger 등록
   - glue 테이블 trigger 테이블 등록
   - quartz trigger 테이블에 등록한다.
   - startOnCreate가 true면 스케줄까지 등록한다.
     scheduler.scheduleJob(jobDetail, trigger);
   - startOnCreate가 true면 
     quartz trigger 테이블에 등록까지만 한다.
     
6. trigger 수정
   - glue 테이블 trigger 테이블 수정
   - quartz trigger 테이블에 수정
   - startOnCreate가 true면 스케줄까지 등록한다.
     scheduler.scheduleJob(jobDetail, trigger);
   - startOnCreate가 true면 
     quartz trigger 테이블에 등록까지만 한다.
     
     
7. trigger 정지
   glue 테이블 에서 삭제한다.
   등록하기전 Action 오브젝트에 jobName가 존재하는지 검증한다.
   quartz의 트리거 테이블에서만 삭제 한다.
   scheduler.unscheduleJob(triggerKey("trigger1", "group1"));
   
8. trigger 삭제
   glue 테이블 에서 삭제한다.
   quartz의 job trigger에서 삭제 한다.
   scheduler.deleteJob(jobKey("job1", "group1"));
      
9. startJobRun 
   job이 즉시 실행됨 하지만 스케줄은 되지 않음.
  


select * from api_glue_job;
select * from api_glue_trigger;
select * from glue_qrtz_blob_triggers;
select * from glue_qrtz_calendars;
select * from glue_qrtz_cron_triggers;
select * from glue_qrtz_fired_triggers;
select * from glue_qrtz_locks;
select * from glue_qrtz_paused_trigger_grps;
select * from glue_qrtz_scheduler_state;
select * from glue_qrtz_simple_triggers;
select * from glue_qrtz_simprop_triggers;
select * from glue_qrtz_triggers;
select * from glue_qrtz_job_details;


delete from glue_qrtz_blob_triggers;
delete from glue_qrtz_calendars;
delete from glue_qrtz_cron_triggers;
delete from glue_qrtz_fired_triggers;
delete from glue_qrtz_locks;
delete from glue_qrtz_paused_trigger_grps;
delete from glue_qrtz_scheduler_state;
delete from glue_qrtz_simple_triggers;
delete from glue_qrtz_simprop_triggers;
delete from glue_qrtz_triggers;
delete from glue_qrtz_job_details;