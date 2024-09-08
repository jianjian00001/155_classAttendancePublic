const base = {
    get() {
        return {
            url : "http://localhost:8080/classAttendance/",
            name: "classAttendance",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/classAttendance/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "高校学生课堂考勤系统"
        } 
    }
}
export default base
