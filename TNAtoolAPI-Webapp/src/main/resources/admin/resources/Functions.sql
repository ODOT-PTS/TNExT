
CREATE OR REPLACE FUNCTION GoogleEncodeLine
(
  g GEOMETRY
)
RETURNS TEXT AS $$
DECLARE
  pt1 GEOMETRY;
  pt2 GEOMETRY;
  p INT; np INT;
  deltaX INT;
  deltaY INT;
  enX VARCHAR(255);
  enY VARCHAR(255);
  gEncoded TEXT;
BEGIN
  gEncoded = '';
  np = ST_NPoints(g);

  IF np > 3 THEN
    np = ST_NPoints(g);
  END IF;

  pt1 = ST_SetSRID(ST_MakePoint(0, 0),4326);

  FOR p IN 1..np BY 1 LOOP
    pt2 = ST_PointN(g, p);
    deltaX = (floor(ST_X(pt2)*1e5)-floor(ST_X(pt1)*1e5))::INT;
    deltaY = (floor(ST_Y(pt2)*1e5)-floor(ST_Y(pt1)*1e5))::INT;
    enX = GoogleEncodeSignedInteger(deltaX);
    enY = GoogleEncodeSignedInteger(deltaY);
    gEncoded = gEncoded || enY || enX;

    pt1 = ST_SetSRID(ST_MakePoint(ST_X(pt2), ST_Y(pt2)),4326);
  END LOOP;
RETURN gEncoded;
End
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION GoogleEncodeSignedInteger(c INT)
RETURNS VARCHAR(255) AS $$
DECLARE
  e VARCHAR(255);
  s BIT(32);
  b BIT(6);
  n INT;
BEGIN
 e = '';
 s = (c::BIT(32))<<1;

 IF s::INT < 0 THEN
   s = ~s;
   END IF;

 WHILE s::INT >= B'100000'::INT LOOP
   b = B'100000' | (('0'||substring(s, 28, 5))::BIT(6));
   n = b::INT + 63;
   e = e || chr(n);
   s = s >> 5;
 END LOOP;
 e = e || chr(s::INT+63);

RETURN e;
End
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION GoogleEncodeSignedInteger(c INT)
RETURNS VARCHAR(255) AS $$
DECLARE
  e VARCHAR(255);
  s BIT(32);
  b BIT(6);
  n INT;
BEGIN
 e = '';
 s = (c::BIT(32))<<1;

 IF s::INT < 0 THEN
   s = ~s;
   END IF;

 WHILE s::INT >= B'100000'::INT LOOP
   b = B'100000' | (('0'||substring(s, 28, 5))::BIT(6));
   n = b::INT + 63;
   e = e || chr(n);
   s = s >> 5;
 END LOOP;
 e = e || chr(s::INT+63);

RETURN e;
End
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION makeuid
(
  r bigint
)
RETURNS TEXT AS $$
DECLARE
  buff TEXT;
  rem int;
  div int;
  BEGIN
  buff = '';
  div = r;
  rem = 0;
while div>0 Loop
if div >92 then
	rem = div%93;
	buff = chr(rem+34)||buff;
	div = div / 93;
else 
	buff = chr(div+34)||buff;
	div = 0;
end if;
end loop;  
RETURN buff;
End
$$ LANGUAGE plpgsql;