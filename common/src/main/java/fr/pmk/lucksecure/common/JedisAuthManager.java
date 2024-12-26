package fr.pmk.lucksecure.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;

import fr.pmk.lucksecure.common.database.LuckSecureDatabase;
import fr.pmk.lucksecure.common.database.UserToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.Context;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;
import redis.clients.jedis.UnifiedJedis;

public abstract class JedisAuthManager extends AuthManager {

        private UnifiedJedis jedis;

        protected JedisAuthManager(Logger logger, LuckSecureDatabase database, LuckPerms luckPerms) {
                super(logger, database, luckPerms);
        }

        public boolean isAuthenticated(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isPresent()) {
                        return this.authentificatedUsers.contains(id.get());
                }
                return false;
        }

        public boolean authenticatedUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.authentificatedUsers.add(id.get());
        }

        public boolean unauthenticatedUser(Audience audience) {
                Optional<UUID> id = audience.get(Identity.UUID);
                if (id.isEmpty()) {
                        throw new IllegalStateException("Audiance UUID can't be empty");
                }
                return this.authentificatedUsers.remove(id.get());
        }

        public void closeJedis() {

        }

}