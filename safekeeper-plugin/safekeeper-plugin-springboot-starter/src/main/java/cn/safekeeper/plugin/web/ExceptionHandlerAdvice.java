package cn.safekeeper.plugin.web;

import cn.safekeeper.common.exception.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 * @author skylark
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

	/**
	 * 参数异常处理返回 状态码:400
	 * @param exception 参数异常
	 * @return 结果
	 */
	@ExceptionHandler({ IllegalArgumentException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result badRequestException(IllegalArgumentException exception) {
		return Result.failedWith(null,CodeEnum.ERROR.getCode(),exception.getMessage());
	}

	
	/**
	 * 权限异常处理
	 * @param exception 权限异常
	 * @return 结果
	 */
	@ExceptionHandler({ SafeKeeperPermissionException.class })
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public Result badMethodExpressException(SafeKeeperPermissionException exception) {
		return Result.failedWith(null,exception.getCode(),exception.getMessage());
	}

	/**
	 * 角色异常处理
	 * @param exception 角色异常
	 * @return 结果
	 */
	@ExceptionHandler(SafeKeeperRoleException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public Result handleError(SafeKeeperRoleException exception) {
		return Result.failedWith(null,exception.getCode(),exception.getMessage());
	}

	/**
	 * 登录异常处理
	 * @param exception 登录异常
	 * @return 结果
	 */
	@ExceptionHandler(SafeKeeperLoginException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result handleError(SafeKeeperLoginException exception) {
		return Result.failedWith(null,exception.getCode(),exception.getMessage());
	}


	/**
	 * 登录异常处理
	 * @param exception 登录异常
	 * @return 结果
	 */
	@ExceptionHandler(SafeKeeperLockedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result handleError(SafeKeeperLockedException exception) {
		return Result.failedWith(null,CodeEnum.ERROR.getCode(),exception.getMessage());
	}

	/**
	 * 404异常处理
	 * @param exception 404异常
	 * @return 结果
	 */
	@ExceptionHandler(ChangeSetPersister.NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Result handleError(ChangeSetPersister.NotFoundException exception) {
		return Result.failedWith(null,CodeEnum.ERROR.getCode(),exception.getMessage());
	}

	/**
	 * 框架异常
	 * @param exception 框架异常
	 * @return 结果
	 */
	@ExceptionHandler(SafeKeeperException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result handleError(SafeKeeperException exception) {
		return Result.failedWith(null,CodeEnum.ERROR.getCode(),exception.getMessage());
	}
}
