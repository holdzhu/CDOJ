package cn.edu.uestc.acmicpc.web.oj.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.edu.uestc.acmicpc.config.TestContext;
import cn.edu.uestc.acmicpc.config.WebMVCConfig;
import cn.edu.uestc.acmicpc.db.criteria.impl.TrainingCriteria;
import cn.edu.uestc.acmicpc.db.dto.field.TrainingFields;
import cn.edu.uestc.acmicpc.db.dto.field.TrainingUserFields;
import cn.edu.uestc.acmicpc.db.dto.impl.TrainingDto;
import cn.edu.uestc.acmicpc.db.dto.impl.TrainingUserDto;
import cn.edu.uestc.acmicpc.db.dto.impl.user.UserDTO;
import cn.edu.uestc.acmicpc.util.enums.AuthenticationType;
import cn.edu.uestc.acmicpc.util.enums.TrainingUserType;
import cn.edu.uestc.acmicpc.util.helper.StringUtil;
import cn.edu.uestc.acmicpc.web.dto.PageInfo;
import cn.edu.uestc.acmicpc.web.oj.controller.training.TrainingController;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mock test for {@link TrainingController}
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestContext.class, WebMVCConfig.class})
public class TrainingControllerTest extends ControllerTest {

  @Autowired
  private TrainingController trainingController;

  @Override
  @BeforeMethod
  public void init() {
    super.init();
    mockMvc = initControllers(trainingController);
    session = new MockHttpSession(context.getServletContext(), UUID.randomUUID().toString());
  }

  @Test
  public void testSearchSuccessful() throws Exception {
    TrainingCriteria trainingCriteria = new TrainingCriteria();
    trainingCriteria.startId = 1;
    trainingCriteria.endId = 3;

    List<TrainingDto> result = new ArrayList<>(3);
    when(trainingService.count(any(TrainingCriteria.class))).thenReturn((long) result.size());
    when(trainingService.getTrainingList(any(TrainingCriteria.class), any(PageInfo.class))).thenReturn(result);

    mockMvc.perform(post("/training/search")
        .contentType(APPLICATION_JSON_UTF8)
        .content(JSON.toJSONBytes(trainingCriteria)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("success")))
        .andExpect(jsonPath("$.list", hasSize(result.size())));

    ArgumentCaptor<TrainingCriteria> trainingCriteriaArgumentCaptor = ArgumentCaptor.forClass(TrainingCriteria.class);
    verify(trainingService).count(trainingCriteriaArgumentCaptor.capture());
    Assert.assertEquals(trainingCriteria.startId, trainingCriteriaArgumentCaptor.getValue().startId);
    Assert.assertEquals(trainingCriteria.endId, trainingCriteriaArgumentCaptor.getValue().endId);
  }

  @Test
  public void testSearchWithNullPostData() throws Exception {
    List<TrainingDto> result = new ArrayList<>(5);
    when(trainingService.count(any(TrainingCriteria.class))).thenReturn((long) result.size());
    when(trainingService.getTrainingList(any(TrainingCriteria.class), any(PageInfo.class))).thenReturn(result);

    mockMvc.perform(get("/training/search"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("success")))
        .andExpect(jsonPath("$.list", hasSize(result.size())));

    ArgumentCaptor<TrainingCriteria> trainingCriteriaArgumentCaptor = ArgumentCaptor.forClass(TrainingCriteria.class);
    verify(trainingService).count(trainingCriteriaArgumentCaptor.capture());
    Assert.assertNull(trainingCriteriaArgumentCaptor.getValue().startId);
    Assert.assertNull(trainingCriteriaArgumentCaptor.getValue().endId);
  }

  @Test
  public void testFetchTrainingDataSuccessful() throws Exception {
    TrainingDto trainingDto = TrainingDto.builder()
        .setTitle("Test")
        .setDescription("Description")
        .setTrainingId(1)
        .build();

    when(trainingService.getTrainingDto(trainingDto.getTrainingId(), TrainingFields.ALL_FIELDS))
        .thenReturn(trainingDto);
    mockMvc.perform(get("/training/data/{trainingId}", trainingDto.getTrainingId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("success")))
        .andExpect(jsonPath("$.trainingDto", notNullValue()))
        .andExpect(jsonPath("$.trainingDto.title", is(trainingDto.getTitle())))
        .andExpect(jsonPath("$.trainingDto.description", is(trainingDto.getDescription())))
        .andExpect(jsonPath("$.trainingDto.trainingId", is(trainingDto.getTrainingId())));
  }

  @Test
  public void testFetchTrainingDataNotFound() throws Exception {
    when(trainingService.getTrainingDto(100, TrainingFields.ALL_FIELDS)).thenReturn(null);

    mockMvc.perform(get("/training/data/{trainingId}", 100))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("error")))
        .andExpect(jsonPath("$.error_msg", is("Training not found!")));
  }

  @Test
  public void testCreateTrainingSuccessful() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingDto trainingEditDto = TrainingDto.builder()
        .setTitle("New training")
        .setDescription("Content")
        .build();
    jsonData.put("trainingEditDto", trainingEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(trainingService.createNewTraining(trainingEditDto.getTitle())).thenReturn(1);
    when(trainingService.getTrainingDto(1, TrainingFields.ALL_FIELDS)).thenReturn(
        TrainingDto.builder()
            .setTrainingId(1)
            .setTitle("")
            .setDescription("")
            .build()
    );
    when(pictureService.modifyPictureLocation(anyString(), anyString(), anyString())).thenReturn(trainingEditDto.getDescription());
    mockMvc.perform(post("/training/edit")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("success")))
        .andExpect(jsonPath("$.trainingId", is(1)));
    ArgumentCaptor<TrainingDto> trainingDtoArgumentCaptor = ArgumentCaptor.forClass(TrainingDto.class);
    verify(trainingService).updateTraining(trainingDtoArgumentCaptor.capture());
    Assert.assertEquals(trainingDtoArgumentCaptor.getValue().getTitle(), trainingEditDto.getTitle());
    Assert.assertEquals(trainingDtoArgumentCaptor.getValue().getDescription(), trainingEditDto.getDescription());
  }

  @Test
  public void testCreateTrainingInvalidTitle() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingDto trainingEditDto = TrainingDto.builder()
        .setTitle("   ")
        .setDescription("Content")
        .build();
    jsonData.put("trainingEditDto", trainingEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    mockMvc.perform(post("/training/edit")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("field_error")))
        .andExpect(jsonPath("$.field", hasSize(1)))
        .andExpect(jsonPath("$.field[0].field", is("title")))
        .andExpect(jsonPath("$.field[0].defaultMessage",
            is("Please enter a validate title.")));
  }

  @Test
  public void testCreateTrainingFailed() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingDto trainingEditDto = TrainingDto.builder()
        .setTitle("New training")
        .setDescription("Content")
        .build();
    jsonData.put("trainingEditDto", trainingEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(trainingService.createNewTraining(trainingEditDto.getTitle())).thenReturn(1);
    when(trainingService.getTrainingDto(1, TrainingFields.ALL_FIELDS)).thenReturn(null);
    mockMvc.perform(post("/training/edit")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("error")))
        .andExpect(jsonPath("$.error_msg", is("Error while creating training.")));
  }

  @Test
  public void testEditTrainingSuccessful() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "edit");
    TrainingDto trainingEditDto = TrainingDto.builder()
        .setTrainingId(1)
        .setTitle("New title")
        .setDescription("Content")
        .build();
    jsonData.put("trainingEditDto", trainingEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(trainingService.createNewTraining(trainingEditDto.getTitle())).thenReturn(1);
    when(trainingService.getTrainingDto(1, TrainingFields.ALL_FIELDS)).thenReturn(
        TrainingDto.builder()
            .setTrainingId(1)
            .setTitle("Old title")
            .setDescription("Old content")
            .build()
    );
    when(pictureService.modifyPictureLocation(anyString(), anyString(), anyString())).thenReturn(trainingEditDto.getDescription());
    mockMvc.perform(post("/training/edit")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("success")))
        .andExpect(jsonPath("$.trainingId", is(1)));
    ArgumentCaptor<TrainingDto> trainingDtoArgumentCaptor = ArgumentCaptor.forClass(TrainingDto.class);
    verify(trainingService).updateTraining(trainingDtoArgumentCaptor.capture());
    Assert.assertEquals(trainingDtoArgumentCaptor.getValue().getTitle(), trainingEditDto.getTitle());
    Assert.assertEquals(trainingDtoArgumentCaptor.getValue().getDescription(), trainingEditDto.getDescription());
  }

  @Test
  public void testEditTrainingFailed() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "edit");
    TrainingDto trainingEditDto = TrainingDto.builder()
        .setTrainingId(1)
        .setTitle("New title")
        .setDescription("Content")
        .build();
    jsonData.put("trainingEditDto", trainingEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(trainingService.createNewTraining(trainingEditDto.getTitle())).thenReturn(1);
    when(trainingService.getTrainingDto(1, TrainingFields.ALL_FIELDS)).thenReturn(null);
    when(pictureService.modifyPictureLocation(anyString(), anyString(), anyString())).thenReturn(trainingEditDto.getDescription());
    mockMvc.perform(post("/training/edit")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("error")))
        .andExpect(jsonPath("$.error_msg", is("Training not found.")));
  }

  @Test
  public void testCreateTrainingUserSuccessful() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName("Ruins He")
        .setType(TrainingUserType.PERSONAL.ordinal())
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(userService.getUserDTOByUserName(trainingUserEditDto.getUserName())).thenReturn(
        UserDTO.builder()
            .setUserId(1)
            .setUserName(trainingUserEditDto.getUserName())
            .build()
    );
    when(trainingUserService.createNewTrainingUser(1, trainingUserEditDto.getTrainingId())).thenReturn(1);
    when(trainingUserService.getTrainingUserDto(1, TrainingUserFields.ALL_FIELDS)).thenReturn(
        TrainingUserDto.builder()
            .setTrainingUserId(1)
            .setUserId(1)
            .setTrainingId(trainingUserEditDto.getTrainingId())
            .build()
    );
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("success")));
    ArgumentCaptor<TrainingUserDto> trainingUserDtoArgumentCaptor = ArgumentCaptor.forClass(TrainingUserDto.class);
    verify(trainingUserService).updateTrainingUser(trainingUserDtoArgumentCaptor.capture());
    Assert.assertEquals(trainingUserDtoArgumentCaptor.getValue().getUserId(), Integer.valueOf(1));
    Assert.assertEquals(trainingUserDtoArgumentCaptor.getValue().getTrainingUserName(), trainingUserEditDto.getTrainingUserName());
    Assert.assertEquals(trainingUserDtoArgumentCaptor.getValue().getType(), trainingUserEditDto.getType());
  }

  @Test
  public void testCreateTrainingUserInvalidTrainingUserName_tooShort() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName("a")
        .setType(TrainingUserType.PERSONAL.ordinal())
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("field_error")))
        .andExpect(jsonPath("$.field", hasSize(1)))
        .andExpect(jsonPath("$.field[0].field", is("trainingUserName")))
        .andExpect(jsonPath("$.field[0].defaultMessage",
            is("Please enter 2-30 characters.")));
  }

  @Test
  public void testCreateTrainingUserInvalidTrainingUserName_tooLong() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName(StringUtil.repeat("a", 40))
        .setType(TrainingUserType.PERSONAL.ordinal())
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("field_error")))
        .andExpect(jsonPath("$.field", hasSize(1)))
        .andExpect(jsonPath("$.field[0].field", is("trainingUserName")))
        .andExpect(jsonPath("$.field[0].defaultMessage",
            is("Please enter 2-30 characters.")));
  }

  @Test
  public void testCreateTrainingUserInvalidUserName() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingId(1)
        .setUserName("aaaaa")
        .setTrainingUserName("Ruins He")
        .setType(TrainingUserType.PERSONAL.ordinal())
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(userService.getUserDTOByUserName(trainingUserEditDto.getUserName())).thenReturn(
        null
    );
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("field_error")))
        .andExpect(jsonPath("$.field", hasSize(1)))
        .andExpect(jsonPath("$.field[0].field", is("userName")))
        .andExpect(jsonPath("$.field[0].defaultMessage",
            is("Invalid OJ user name.")));
  }

  @Test
  public void testCreateTrainingUserInvalidType_less() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName("Ruins He")
        .setType(-1)
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(userService.getUserDTOByUserName(trainingUserEditDto.getUserName())).thenReturn(
        UserDTO.builder()
            .setUserId(1)
            .setUserName(trainingUserEditDto.getUserName())
            .build()
    );
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("field_error")))
        .andExpect(jsonPath("$.field", hasSize(1)))
        .andExpect(jsonPath("$.field[0].field", is("type")))
        .andExpect(jsonPath("$.field[0].defaultMessage",
            is("Invalid type.")));
  }

  @Test
  public void testCreateTrainingUserInvalidType_more() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName("Ruins He")
        .setType(TrainingUserType.values().length)
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(userService.getUserDTOByUserName(trainingUserEditDto.getUserName())).thenReturn(
        UserDTO.builder()
            .setUserId(1)
            .setUserName(trainingUserEditDto.getUserName())
            .build()
    );
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("field_error")))
        .andExpect(jsonPath("$.field", hasSize(1)))
        .andExpect(jsonPath("$.field[0].field", is("type")))
        .andExpect(jsonPath("$.field[0].defaultMessage",
            is("Invalid type.")));
  }

  @Test
  public void testCreateTrainingUserFailed() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "new");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName("Ruins He")
        .setType(TrainingUserType.PERSONAL.ordinal())
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(userService.getUserDTOByUserName(trainingUserEditDto.getUserName())).thenReturn(
        UserDTO.builder()
            .setUserId(1)
            .setUserName(trainingUserEditDto.getUserName())
            .build()
    );
    when(trainingUserService.createNewTrainingUser(1, trainingUserEditDto.getTrainingId())).thenReturn(1);
    when(trainingUserService.getTrainingUserDto(1, TrainingUserFields.ALL_FIELDS)).thenReturn(
        null
    );
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("error")))
        .andExpect(jsonPath("$.error_msg", is("Error while creating training user.")));
  }

  @Test
  public void testEditTrainingUserSuccessful() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "edit");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingUserId(1)
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName("Ruins He 2nd Edition")
        .setType(TrainingUserType.PERSONAL.ordinal())
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(userService.getUserDTOByUserName(trainingUserEditDto.getUserName())).thenReturn(
        UserDTO.builder()
            .setUserId(1)
            .setUserName(trainingUserEditDto.getUserName())
            .build()
    );
    when(trainingUserService.getTrainingUserDto(1, TrainingUserFields.ALL_FIELDS)).thenReturn(
        TrainingUserDto.builder()
            .setTrainingUserId(1)
            .setUserId(1)
            .setTrainingId(trainingUserEditDto.getTrainingId())
            .setTrainingUserName("Ruins He")
            .build()
    );
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("success")));
    ArgumentCaptor<TrainingUserDto> trainingUserDtoArgumentCaptor = ArgumentCaptor.forClass(TrainingUserDto.class);
    verify(trainingUserService).updateTrainingUser(trainingUserDtoArgumentCaptor.capture());
    Assert.assertEquals(trainingUserDtoArgumentCaptor.getValue().getUserId(), Integer.valueOf(1));
    Assert.assertEquals(trainingUserDtoArgumentCaptor.getValue().getTrainingUserName(), trainingUserEditDto.getTrainingUserName());
    Assert.assertEquals(trainingUserDtoArgumentCaptor.getValue().getType(), trainingUserEditDto.getType());
  }

  @Test
  public void testEditTrainingUserFailed() throws Exception {
    Map<String, Object> jsonData = new HashMap<>();
    jsonData.put("action", "edit");
    TrainingUserDto trainingUserEditDto = TrainingUserDto.builder()
        .setTrainingUserId(2)
        .setTrainingId(1)
        .setUserName("Administrator")
        .setTrainingUserName("Ruins He 2nd Edition")
        .setType(TrainingUserType.PERSONAL.ordinal())
        .build();
    jsonData.put("trainingUserEditDto", trainingUserEditDto);
    String jsonDataString = JSON.toJSONString(jsonData);
    UserDTO currentUserDTO = UserDTO.builder()
        .setType(AuthenticationType.ADMIN.ordinal())
        .setUserId(100)
        .build();
    when(userService.getUserDTOByUserName(trainingUserEditDto.getUserName())).thenReturn(
        UserDTO.builder()
            .setUserId(1)
            .setUserName(trainingUserEditDto.getUserName())
            .build()
    );
    when(trainingUserService.getTrainingUserDto(2, TrainingUserFields.ALL_FIELDS)).thenReturn(
        null
    );
    mockMvc.perform(post("/training/editTrainingUser")
        .sessionAttr("currentUser", currentUserDTO)
        .contentType(APPLICATION_JSON_UTF8)
        .content(jsonDataString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is("error")))
        .andExpect(jsonPath("$.error_msg", is("Training user not found.")));
  }
}