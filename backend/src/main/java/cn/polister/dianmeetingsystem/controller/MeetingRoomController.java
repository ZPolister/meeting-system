package cn.polister.dianmeetingsystem.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.dto.MeetingRoomDto;
import cn.polister.dianmeetingsystem.entity.vo.MeetingRoomVo;
import cn.polister.dianmeetingsystem.service.MeetingRoomService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "会议室管理模块")
@RestController
@RequestMapping("/api/meeting-room")
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;

    // 新增会议室
    @Operation(summary = "新增会议室", description = "新增会议室")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    @PostMapping
    public ResponseResult<Void> addMeetingRoom(@RequestBody MeetingRoomDto meetingRoom) {
        meetingRoomService.saveWithTimeSlots(meetingRoom);
        return ResponseResult.okResult();
    }

    // 修改会议室
    @Operation(summary = "修改会议室", description = "修改会议室")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    @PutMapping
    public ResponseResult<Void> updateMeetingRoom(@RequestBody MeetingRoom meetingRoom) {
        meetingRoomService.updateById(meetingRoom);
        return ResponseResult.okResult();
    }

    // 删除会议室
    @Operation(summary = "分页获取会议室列表", description = "分页查询会议室信息，支持类型和状态筛选")
    @GetMapping("/page")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    public ResponseResult<Page<MeetingRoomVo>> getMeetingRoomsByPage(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) String roomStatus) {

        return ResponseResult.okResult(
            meetingRoomService.getMeetingRoomsByPage(pageNum, pageSize, roomType, roomStatus)
        );
    }

    @Operation(summary = "删除会议室", description = "删除会议室")
    @SaCheckRole(UserConstants.USER_ROLE_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteMeetingRoom(@PathVariable Long id) {
        meetingRoomService.deleteMeetingRoom(id);
        return ResponseResult.okResult();
    }
}