package me.qidongs.rootwebsite.dao;

import me.qidongs.rootwebsite.model.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
@Deprecated
public interface LoginTicketDao {
    @Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})",})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    @Select({"select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update login_ticket set status=#{status} where ticket=#{ticket} "

            })
    int updateStatus(String ticket, int status);


}

