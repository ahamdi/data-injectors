package org.exoplatform.services.injection.social;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.social.core.space.impl.DefaultSpaceApplicationHandler;
import org.exoplatform.social.core.space.model.Space;

import java.util.HashMap;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class SpaceInjector extends AbstractSocialInjector {

  private final int FLUSH_LIMIT = 10;

  /** . */
  private static final String NUMBER = "number";

  /** . */
  private static final String FROM_USER = "fromUser";

  /** . */
  private static final String TO_USER = "toUser";

  /** . */
  private static final String USER_PREFIX = "userPrefix";

  /** . */
  private static final String SPACE_PREFIX = "spacePrefix";
  
  public SpaceInjector(PatternInjectorConfig pattern) {
    super(pattern);
  }

  @Override
  public void inject(HashMap<String, String> params) throws Exception {

    //
    int number = param(params, NUMBER);
    int from = param(params, FROM_USER);
    int to = param(params, TO_USER);
    String userPrefix = params.get(USER_PREFIX);
    String spacePrefix = params.get(SPACE_PREFIX);
    init(userPrefix, spacePrefix, userSuffixValue, spaceSuffixValue);
    
    int spaceCounter = 0;
    try {
      //
      for(int i = from; i <= to; ++i) {
        for (int j = 0; j < number; ++j) {

          //
          String owner = this.userNameSuffixPattern(i);
          String spaceName = spaceName();

          Space space = new Space();
          space.setDisplayName(spaceName);
          space.setPrettyName(spaceName);
          space.setGroupId("/spaces/" + space.getPrettyName());
          space.setRegistration(Space.OPEN);
          space.setDescription(lorem.getWords(10));
          space.setType(DefaultSpaceApplicationHandler.NAME);
          space.setVisibility(Space.PRIVATE);
          space.setRegistration(Space.OPEN);
          space.setPriority(Space.INTERMEDIATE_PRIORITY);
          space.setEditor(owner);

          //
          spaceService.createSpace(space, owner);
          ++spaceNumber;
          if (++spaceCounter == FLUSH_LIMIT) {
            spaceCounter = 0;
            //
            RequestLifeCycle.end();
            RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
            getLog().info("Flush session...");
          }
          //
          getLog().info("Space " + spaceName + " created by " + owner);

        }
      }
    } finally {
        RequestLifeCycle.end();
        RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
    }
    
    
  }
}
