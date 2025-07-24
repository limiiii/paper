page6:checkemail, sendemail and comfirm auth_code
//上游操作：已经校验完成验证码（调用春明接口完成验证），后面继续输入两次密码，前端将邮箱号和两次的密码一同传输给我：1.校验两次输入的密码是否一致；2.输入的密码和保存的密码是否重复；若都通过就落库返回成功
//Request：1.resetEmail 2.resetPassword1 3.resetPassword2
//Response:{"code":"0", "msg":"success", "data":{"密码设置成功！"}}


//ResetPasswordRequestVO.java
public class ResetPasswordRequestVO{
    private String resetEmail;
    private String resetPassword1;
    private String resetPassword2;
    ResetPasswordVO(){}
    ResetPasswordVO(String resetEmail, String resetPassword1, String Password2){
        this.resetEmail = resetEmail;
        this.resetPassword1 = resetPassword1;
        this.resetPassword2 = resetPassword2;
    }
    public String getEmail() {
        return resetEmail;
    }
    public void setResetEmail(String resetEmail) {
        this.resetEmail = resetEmail;
    }
    public String getResetPassword1() {
        return resetPassword1;
    }
    public void setResetPassword1(String resetPassword1) {
        this.resetPassword1 = resetPassword1;
    }
    public String getResetPassword2() {
        return resetPassword2;
    }
    public void setResetPassword2(String resetPassword2) {
        this.resetPassword2 = resetPassword2;
    }
}

//ResetPasswordResponseVO.java
public class ResetPasswordResponseVO {
    private String res;

    public ResetPasswordResponseVO() {}
    public ResetPasswordResponseVO(String res) {
        this.res = res;
    }
    public String getRes() {
        return res;
    }
    public void setRes(String res) {
        this.res = res;
    }
}
//ResetPasswordRequestDTO.java
public class ResetPasswordRequestDTO{
    private String email;
    private String password1;
    private String password2;
    ResetPasswordDTO(){}
    ResetPasswordDTO(String email, String password1, String password2){
        this.email = email;
        this.password1 = password1;
        this.password2 = password2;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword1() {
        return password1;
    }
    public void setPassword1(String password1) {
        this.password1 = password1;
    }
    public String getPassword2() {
        return password2;
    }
    public void setPassword(String password2) {
        this.password2 = password2;
    }
}
//ResetPasswordResponseDTO.java
public class ResetPasswordResponseDTO {
    private String res;

    public ResetPasswordResponseDTO() {}
    public ResetPasswordResponseDTO(String res) {
        this.res = res;
    }
    public String getRes() {
        return res;
    }
    public void setRes(String res) {
        this.res = res;
    }
}


//ResetPasswordRequestDAO.java
public class ResetPasswordRequestDAO{
    private String email;
    private String password;
    ResetPasswordDAO(){}
    ResetPasswordDAO(String email, String password){
        this.email = email;
        this.password1 = password;
    }
    public String Email() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
//ResetPasswordResponseDAO.java
public class ResetPasswordResponseDAO {
    private String res;

    public ResetPasswordResponseDAO() {}
    public ResetPasswordResponseDAO(String res) {
        this.res = res;
    }
    public String getRes() {
        return res;
    }
    public void setRes(String res) {
        this.res = res;
    }
}


//ResetPasswordController.java
@RestController
@RequestMapping("/resetpassword")
public class ResetPasswordController {

    @Autowired
    private ResetPasswordService resetPasswordService;

    @PostMapping("/reset")
    public Result<ResetPasswordResponseVO> resetPassword(@RequestBody ResetPasswordRequestVO requestVO) {
        // VO → DTO
        ResetPasswordRequestDTO requestdto = new ResetPasswordDTO();
        requestdto.setEmail(requestVO.getResetPassword1());
        requestdto.setPassword1(requestVO.getResetPassword1());
        requestdto.setPassword2(requestVO.getResetPassword2());

        //接收service回传的dto
        ResetPasswordResponseDTO responsedto = resetPasswordService.resetPassword(requestdto);
        // DTO → VO 
        ResetPasswordResponseVO resvo = new ResetPasswordResponseVO();
        resvo.setRes(responsedto.getRes());

        //根据字段是否成功返回result.success(0, msg, res)，失败时返回result.fail(400, res)

        if(rersvo.getRes=="请求失败，输入的两次密码不一致！" || rersvo.getRes=="请求失败，新密码与原密码相同，请重新输入！"){
            Result<ResetPasswordResponseVO> result = new Result<ResetPasswordResponseVO>();
            return result.fail(400, resvo.getRes());
        }
        Result<ResetPasswordResponseVO> result = new Result<ResetPasswordResponseVO>();
        return result.success(0, resvo.getRes());

    }
}

//ResetPasswordService.java
public interface ResetPasswordService {
    ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO dto);
}

//ResetPasswordServiceImpl.java
@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    @Autowired
    private ResetPasswordMapper resetPasswordMapper;

    @Override
    public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO dto) {
        // 校验两次输入的密码是否一致
        if (!dto.getPassword1().equals(dto.getPassword2())) {
            return new ResetPasswordResponseDTO("请求失败，输入的两次密码不一致！");
        }
        // 新密码与旧密码进行对比
        if(dto.getPassword1().equals(resetPasswordMapper.GetOriginPassword(dto.getEmail()))){
            return new ResetPasswordResponseDTO("请求失败，新密码与原密码相同，请重新输入！");
        }
        resetPasswordMapper.ResetPassword(dto.getPassword1());
        return new ResetPasswordResponseVO("密码设置成功！");
    }
}

//ResetPasswordMapper.java
@Mapper
public interface ResetPasswordMapper{
    String GetOriginPassword(@Pararm("Email") String Email);
    ResetPasswordResponseDAO ResetPassword(@Pararm("newPassword") String newPassword);
}

//ResetPasswordDAO.xml
<update id="ResetPassword" parameterType="ResetPasswordRequestDAO">
    update invesst_account set account_password = #{newPassword} where email = #{email}
</update>
<select id="GetOriginPassword" parameterType="String" resultType="String">
    select account_password from account_password where where email = #{email}
</select>