export interface LoginRequest {
	login: string
	password: string
}

export interface LoginResponse {
	token: string
	role: string
	fullName: string
}

export interface UserProfile {
	id: number
	fullName: string
	login: string
	role: string
}

export interface CreateAdminRequest {
	login: string
	password: string
}

export interface TeacherInfo {
	id: number
	fullName: string
	login: string
}

export interface CreateTeacherRequest {
	fullName: string
	login: string
	password: string
}

export interface UpdateTeacherRequest {
	fullName: string
	login?: string
	password?: string
}

export interface TeacherResponse {
	id: number
	fullName: string
	login: string
	password: string
}

export interface StudentResponse {
	id: number
	userId?: number
	login?: string
	lastName: string
	firstName: string
	middleName?: string
	birthDate?: string
	inn?: string
	phone?: string
}

export interface CreateStudentRequest {
	login: string
	password: string
	lastName: string
	firstName: string
	middleName?: string
	birthDate?: string
	inn?: string
	phone?: string
}

export interface UpdateStudentRequest {
	login?: string
	password?: string
	lastName: string
	firstName: string
	middleName?: string
	birthDate?: string
	inn?: string
	phone?: string
}

export interface GroupResponse {
	id: number
	name: string
	students: StudentResponse[]
}

export interface CreateGroupRequest {
	name: string
}

export interface AddStudentsRequest {
	studentIds: number[]
}

export interface SubjectResponse {
	id: number
	name: string
}

export interface CreateSubjectRequest {
	name: string
}

export interface LessonResponse {
	id: number
	subjectName: string
	dateTime: string
	groupName: string
	groupId: number
	teacherId: number | null
	teacherName: string | null
	classroom: string
	durationMinutes: number
}

export interface CreateLessonRequest {
	subjectId: number
	dateTime: string
	groupId: number
	teacherId?: number
	classroom: string
	durationMinutes?: number
}

export interface AttendanceResponse {
	id: number
	studentId: number
	studentName: string
	lessonId: number
	present: boolean
}

export interface LessonAttendanceSummary {
	lesson: LessonResponse
	totalStudents: number
	presentCount: number
	attendancePercentage: number
	records: AttendanceResponse[]
}

export interface ParentResponse {
	id: number
	userId: number
	login: string
	lastName: string
	firstName: string
	middleName?: string
	phone?: string
	child: StudentResponse
}

export interface CreateParentRequest {
	login: string
	password: string
	lastName: string
	firstName: string
	middleName?: string
	phone?: string
	studentId: number
}
