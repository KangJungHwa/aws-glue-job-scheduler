api_glue_job_run 테이블 job_run_state 코드
 - STARTING | RUNNING | STOPPING | STOPPED | SUCCEEDED | FAILED | TIMEOUT
api_glue_trigger 테이블 trigger_state 코드
 - STANDBY | RUNNING |  STOPPED 
api_glue_trigger 테이블 type 코드
 - SCHEDULED | ON_DEMAND 





1. job 등록 (createJob)
   - api_glue_job, glue_sch_qrtz_job_details 테이블에 등록
   - job 등록 시 glue는 job group 개념이 없기 때문에 
      - userName을 group 컬럼에 등록하게 함.
   
2. job 실행 (startJobRun)
   - api_glue_job, glue_sch_qrtz_job_details 테이블에 존재해야함.
   - 스케줄링 없이 한번만 실행한다.
   - api_glue_job_run에 등록됨
     - job_run_state 필드가 "START"로 입력 후 "COMPLETE"로 변경됨(insert and update)
       - 상태값은 glue api를 조사해서 동일하게 사용할 것
   - api_glue_job_run 테이블의 trigger_Id는 null 상태 임.
   
3. create trigger 
   - api_glue_job, glue_sch_qrtz_job_details 테이블에 존재해야함.
   - api_glue_trigger 테이블에 저장함.
   - startOnCreate : false
      - api_glue_trigger 테이블에 저장하고 종료 함.
      - quartz 테이블에 등록하지 않는 이유는 등록과 동시에 스케줄링이 됨.
     startOnCreate : true
      - glue_sch_qrtz_triggers 테이블에 저장됨과 동시에 스케줄러 start
      - api_glue_job_run 에 데이터를 생성한다.
        - triggerId가 입력됨
        - job_run_state "STRAT"로 입력 후 "RUNNING"으로 update 됨
   참고 : 
      glue는 하나의 트리거에 여러개의 jobName을 실행 할 수 있는데
      quartz는 하나에만 입력할 수 있게 되어 있어서 일단 하나만 입력하고
      StartJob 클래스에서 실행시에는 triggerName으로 
      glue api_glue_trigger 테이블에 Action 컴럼을 조회해서 실행한다.
      loop를 돌려서 순서 대로 돌아간다.
      - 이때 Job이 실행 될때 마다 job_run 테이블에 입력하고 pid를 저장한다.
   
4. start trigger 
   - api_glue_job, glue_sch_qrtz_job_details, api_glue_trigger 테이블에 존재해야함.
   - create trigger 에서 startOnCreate이 false로 생성한 경우다.
   - 이미 3번에서 job과 trigger를 저장하였으므로 quartz 스케줄만 start 시켜준다. 
     api-server에서 수행하지 않고 schedule-server로 addTrigger 메소드만 호출해준다.
   - api_glue_job_run에 triggerId입력되고 status가 "START"로 입력 후 "RUNNING" update 됨
   
5. stop trigger 
   - api_glue_job, glue_sch_qrtz_job_details,api_glue_trigger 테이블에 존재해야함.
   - api_glue_job_run테이블과 api_glue_trigger의 state컬럼을 "STOP"으로 변경
   - quartz 스케줄링에서 제외한다.

6. delete trigger 
   - api_glue_job, glue_sch_qrtz_job_details,api_glue_trigger 테이블에 존재해야함.
   - quartz 스케줄이 걸려 있는경우
     - 삭제 할 수 없음
   - quartz 스케줄이 없는경우
     - 삭제 가능("STANDBY" 또는 "STOP"인 경우만 삭제 가능)
	 
7. update trigger
 - 스케줄이 걸려 있지 않은 경우는 
   - api_glue_job, api_glue_trigger만 수정하면 됨.
 - 스케줄이 걸려 있는 경우는?
    스케줄이 걸려 있는 경우는 수정이 안됨 
    사용자가 stop trigger 호출 후 Update 해야함.
	"STANDBY" 또는 "STOP"인 경우만 update 할 수 있다.

8. Delete job
 - 스케줄이 걸려 있지 않은 경우
   api_glue_job, glue_sch_qrtz_job_details 삭제
 - 스케줄이 걸려 있는 경우
   해당 job이 trigger에서 사용하고 있으면 삭제 않됨. 
   list trigger로 조회하여 job이 포함된 트리거가 없을때만 가능
   해당 Job 사용 trigger "STOP" 후 해당 trigger 삭제한 후 Job Delete 가능
	   
9. UpdateJob
  api_glue_job 테이블 수정
     - scriptLocation, Scriptname 등
  jobName 컬럼은 수정 시 기존에 걸려있는 Trigger에 영향이 크므로 jobName은 수정은 불가
  jobName의 수정이 필요한 경우 job 삭제 후 재등록 
  해당 job이 trigger에서 deactivate(상태값 찾아볼것)인 경우만 update 가능
	ListTrigger로 job이 포함된 trigger의 리스트 조회 후 
	GetTrigger Action으로 조회한 후에 상태값이 "STANDBY" 또는 "STOP"인 경우만 수정 가능

10. BatchStopJobRun
    jobRunId로 jobRun Table을 조회하여 "RUNNING" jobRunId의 pid를 kill 한다.. 
	 
	 
  


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