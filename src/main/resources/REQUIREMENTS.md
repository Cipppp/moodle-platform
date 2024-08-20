# Final Project

Beforehand you should create a branch using the team name and the homework module should be used.

We thought that it would be nice to put together all the information that you have gathered since DevSchool started.  
Your target is to build an application from scratch which will contain backend implementation. The idea is to build a course management platform, which should have the following features:

The student should:
- Have the possibility to login – Use Spring Security and the password should not be saved in plaintext
- Be able to enrol to multiple courses
- Be able to see his enrolled courses and also the grades
- Not be able to enrol to multiple courses in the same time frame
- Not be able to enrol to a course that's already full

The teacher should:
- Have the possibility to login – Use Spring Security and the password should not be saved in plaintext
- Be able to create multiple courses: establish the schedule

```json
{
	"name": "OOP",
	"description": "Oriented object programming basic",
	"maxAttendees": 30,
	"schedule": [
		{
			"name": "course",
			"startDate": "11.01.2023",
			"endDate": "01.06.2023",
			"weekDay": "MON",
			"startTime": "10:00",
			"endTime": "12:00"
		},
		{
			"name": "laboratory",
			"startDate": "11.01.2023",
			"endDate": "01.06.2023",
			"weekDay": "TUE",
			"startTime": "13:00",
			"endTime": "15:00"
		}
	]
}
```
- Be able to approve/deny student enrolment requests
- Be able to grade students: ONLY one grade per course per student

### Bonus
- use postman collection request chaining feature: pass a response parameter to the next request automatically instead of doing it manually
- test the business logic (the services)

You have to think of a way to design the database and apis. Feel free to create/add any validation you like in backend. You should prepare also a postman collection which will contain all your created endpoints.

