CREATE PROCEDURE dbo.uspLogin
    @pName NVARCHAR(254),
    @pPassword NVARCHAR(50),
    @responseMessage NVARCHAR(250)='' OUTPUT
AS
BEGIN

    SET NOCOUNT ON

    DECLARE @userID INT

    IF EXISTS (SELECT ID FROM USR01_USER WHERE NAME=@pName)
    BEGIN
        SET @userID=(SELECT ID FROM USR01_USER WHERE NAME=@pName AND PASSWORD=HASHBYTES('SHA2_512', @pPassword+CAST(Salt AS NVARCHAR(36))))

       IF(@userID IS NULL)
           SET @responseMessage='INVALID_PW'
       ELSE
           SET @responseMessage='SUCCESS'
    END
    ELSE
       SET @responseMessage='INVALID_LOGIN'

END

