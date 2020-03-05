package io.wisoft.daewon.namingstrategy;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {

  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    final LinkedList<String> parts = splitAndReplace(name.getText());
    return jdbcEnvironment.getIdentifierHelper().toIdentifier(
        join(parts),
        name.isQuoted()
    );
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return name;
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    final LinkedList<String> parts = splitAndReplace(name.getText());
    return jdbcEnvironment.getIdentifierHelper().toIdentifier(
        join(parts),
        name.isQuoted()
    );
  }

  private LinkedList<String> splitAndReplace(String name) {
    LinkedList<String> result = new LinkedList<>();
    for (String part : StringUtils.splitByCharacterTypeCamelCase(name)) {
      if (part == null || part.trim().isEmpty()) {
        continue;
      }
      result.add(part.toLowerCase(Locale.ROOT));
    }
    return result;
  }

  private String join(List<String> parts) {
    boolean firstPass = true;
    String separator = "";
    StringBuilder joined = new StringBuilder();
    for (String part : parts) {
      joined.append(separator).append(part);
      if (firstPass) {
        firstPass = false;
        separator = "_";
      }
    }
    return joined.toString();
  }

}

